package com.gener.chat.services;

import com.gener.chat.dtos.response.ExtractFaceResult;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.FaceProfile;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.models.User;
import com.gener.chat.repositories.FaceProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class FaceProfileService {

    private final UserService userService;
    private final FaceProfileRepository faceProfileRepository;
    private final FaceClientService faceClientService;
    private final ObjectMapper objectMapper;

    public ResponseEntity<ResponseObject> register(MultipartFile file) throws APIException {
        try {
            User currentUser = userService.getUserFromToken();
            ExtractFaceResult extractResult = faceClientService.extractEmbedding(file);
            String embeddingJson = objectMapper.writeValueAsString(extractResult.getEmbedding());

            FaceProfile profile = faceProfileRepository.findByUserId(currentUser.getId())
                    .orElse(FaceProfile.builder()
                            .user(currentUser)
                            .registered(false)
                            .build());

            profile.setEmbedding(embeddingJson);
            profile.setRegistered(true);

            faceProfileRepository.save(profile);

            return ResponseEntity.ok(
                    ResponseObject.builder()
                            .status(200)
                            .message("Đăng ký khuôn mặt thành công")
                            .data(true)
                            .build()
            );
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<ResponseObject> me() throws APIException {
        User currentUser = userService.getUserFromToken();
        boolean registered = faceProfileRepository.existsByUserIdAndRegisteredTrue(currentUser.getId());

        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(200)
                        .message("Thông tin khuôn mặt")
                        .data(registered)
                        .build()
        );
    }
}