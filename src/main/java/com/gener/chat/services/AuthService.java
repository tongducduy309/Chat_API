package com.gener.chat.services;

import com.gener.chat.dtos.request.FaceSearchCandidate;
import com.gener.chat.dtos.request.IntrospectReq;
import com.gener.chat.dtos.request.LoginReq;
import com.gener.chat.dtos.response.FaceSearchResult;
import com.gener.chat.dtos.response.LoginRes;
import com.gener.chat.dtos.response.TokenRes;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.enums.SuccessCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.FaceProfile;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.FaceProfileRepository;
import com.gener.chat.repositories.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final FaceClientService faceClientService;

    private final FaceProfileRepository faceProfileRepository;

    private final ObjectMapper objectMapper;


    @Value("${jwt.secret}")
    private String SIGNER_KEY;

    @Value("${jwt.expiration-ms}")
    private int EXPIRATION_TOKEN;

    @Value("${jwt.issuer}")
    private String ISSUER;

    @Transactional
    public ResponseEntity<ResponseObject> login(LoginReq req) throws APIException {
        if (req.getEmailOrPhone()==null) throw new APIException(ErrorCode.PHONE_OR_EMAIL_REQUIRED);
        User user = userRepository.findByPhoneOrEmail(req.getEmailOrPhone()).orElseThrow(()-> new APIException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(req.getPassword(),user.getPasswordHash())) throw new APIException(ErrorCode.PASSWORD_INCORRECT);

        TokenRes tokenRes = generateToken(user);
        return ResponseEntity.status(SuccessCode.LOGIN_SUCCESS.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.LOGIN_SUCCESS.getStatus())
                        .message(SuccessCode.LOGIN_SUCCESS.getMessage())
                        .data(
                                LoginRes.builder()
                                        .accessToken(tokenRes.getAccessToken())
                                        .expiresAt(tokenRes.getExpiresAt())
                                        .tokenType(tokenRes.getTokenType())
                                        .expiresIn(tokenRes.getExpiresIn())
                                        .build()
                        )
                .build()
        );

    }

    @Transactional
    public ResponseEntity<ResponseObject> introspect(IntrospectReq introspectReq) throws APIException {
        String token = introspectReq.getToken();
        if (token == null || token.isBlank()) {
            throw new APIException(ErrorCode.TOKEN_MISSING);
        }

        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            boolean verified = signedJWT.verify(verifier);
            if (!verified) {
                throw new APIException(ErrorCode.TOKEN_INVALID);
            }

            Date expiredTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expiredTime == null || !expiredTime.after(new Date())) {
                throw new APIException(ErrorCode.TOKEN_EXPIRED);
            }

            return ResponseEntity.status(SuccessCode.TOKEN_VALID.getHttpStatusCode()).body(
                    new ResponseObject(
                            SuccessCode.TOKEN_VALID.getStatus(),
                            SuccessCode.TOKEN_VALID.getMessage(),
                            true
                    )
            );

        } catch (ParseException | JOSEException e) {
            throw new APIException(ErrorCode.TOKEN_INVALID);
        }
    }

    public TokenRes generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        Instant now = Instant.now();
        Instant expiryInstant = now.plus(EXPIRATION_TOKEN, ChronoUnit.HOURS);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getId()+"")
                .issuer(ISSUER)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(expiryInstant))
                .claim("scope",buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            String token = jwsObject.serialize();
            return TokenRes.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(EXPIRATION_TOKEN * 3600)
                    .expiresAt(expiryInstant)
                    .build();
        } catch (JOSEException e) {
//            logger.error("Cannot Create Token",e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
//        if (!CollectionUtils.isEmpty(user.getRoles())){
//            user.getRoles().forEach(stringJoiner::add);
//        }
        return stringJoiner.toString();
    }

    @Transactional
    public ResponseEntity<ResponseObject> faceLogin(MultipartFile file) throws APIException {
        List<FaceProfile> profiles = faceProfileRepository.findAllByRegisteredTrue();

        if (profiles.isEmpty()) {
            throw new APIException(ErrorCode.FACE_LOGIN_FAILED);
        }

        List<FaceSearchCandidate> candidates = profiles.stream()
                .map(profile -> FaceSearchCandidate.builder()
                        .userId(profile.getUser().getId())
                        .embedding(readEmbedding(profile.getEmbedding()))
                        .build())
                .toList();

        FaceSearchResult searchResult = faceClientService.searchFace(file, candidates);

        if (!Boolean.TRUE.equals(searchResult.getMatched()) || searchResult.getUserId() == null) {
            throw new APIException(ErrorCode.FACE_LOGIN_FAILED);
        }

        User user = userRepository.findById(searchResult.getUserId())
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        String accessToken = generateToken(user).getAccessToken();
//        String refreshToken = jwtService.generateRefreshToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("accessToken", accessToken);
//        data.put("refreshToken", refreshToken);

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(200)
                        .message("Đăng nhập bằng khuôn mặt thành công")
                        .data(data)
                        .build()
        );
    }

    private List<Double> readEmbedding(String embeddingJson) {
        try {
            return objectMapper.readValue(
                    embeddingJson,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Double.class)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
