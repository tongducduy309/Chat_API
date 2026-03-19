package com.gener.chat.services;

import com.gener.chat.dtos.request.FaceSearchCandidate;
import com.gener.chat.dtos.response.ExtractFaceResult;
import com.gener.chat.dtos.response.FaceSearchResult;
import com.gener.chat.dtos.response.FaceVerifyResult;
import com.gener.chat.dtos.response.FastApiResponse;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.exception.APIException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaceClientService {

    @Value("${face.service.url}")
    private String baseUrl;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExtractFaceResult extractEmbedding(MultipartFile file) throws APIException {
        try {
            HttpEntity<MultiValueMap<String, Object>> requestEntity = buildFileRequest(file);

            ResponseEntity<FastApiResponse<ExtractFaceResult>> response = restTemplate.exchange(
                    baseUrl + "/face/extract",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FastApiResponse<ExtractFaceResult>>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().getData() == null) {
                throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
            }

            return response.getBody().getData();
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
        }
    }

    public FaceVerifyResult verifyFace(MultipartFile file, String embeddingJson) throws APIException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<ByteArrayResource> fileEntity = buildFilePart(file);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileEntity);
            body.add("embedding", embeddingJson);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<FastApiResponse<FaceVerifyResult>> response = restTemplate.exchange(
                    baseUrl + "/face/verify",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FastApiResponse<FaceVerifyResult>>() {}
            );

            FastApiResponse<FaceVerifyResult> responseBody = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
            }

            if (Boolean.FALSE.equals(responseBody.getSuccess())) {
                throw buildFastApiMessageException(responseBody.getMessage());
            }

            if (responseBody.getData() == null) {
                throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
            }

            return responseBody.getData();

        } catch (HttpStatusCodeException e) {
            throw extractFastApiException(e);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
        }
    }

    public FaceSearchResult searchFace(MultipartFile file, List<FaceSearchCandidate> candidates) throws APIException {
        try {
            String candidatesJson = objectMapper.writeValueAsString(candidates);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<ByteArrayResource> fileEntity = buildFilePart(file);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileEntity);
            body.add("candidates", candidatesJson);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<FastApiResponse<FaceSearchResult>> response = restTemplate.exchange(
                    baseUrl + "/face/search",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<FastApiResponse<FaceSearchResult>>() {}
            );

            FastApiResponse<FaceSearchResult> responseBody = response.getBody();

            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
            }

            if (Boolean.FALSE.equals(responseBody.getSuccess())) {
                throw buildFastApiMessageException(responseBody.getMessage());
            }

            if (responseBody.getData() == null) {
                throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
            }

            return responseBody.getData();

        } catch (HttpStatusCodeException e) {
            throw extractFastApiException(e);
        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new APIException(ErrorCode.FACE_SERVICE_ERROR);
        }
    }

    private APIException extractFastApiException(HttpStatusCodeException e) {
        try {
            String responseBody = e.getResponseBodyAsString();

            FastApiResponse<Object> fastApiResponse = objectMapper.readValue(
                    responseBody,
                    new TypeReference<FastApiResponse<Object>>() {}
            );

            if (fastApiResponse.getMessage() != null && !fastApiResponse.getMessage().isBlank()) {
                return buildFastApiMessageException(fastApiResponse.getMessage());
            }

            return new APIException(ErrorCode.FACE_SERVICE_ERROR);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new APIException(ErrorCode.FACE_SERVICE_ERROR);
        }
    }

    private HttpEntity<MultiValueMap<String, Object>> buildFileRequest(MultipartFile file) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<ByteArrayResource> fileEntity = buildFilePart(file);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);

        return new HttpEntity<>(body, headers);
    }

    private HttpEntity<ByteArrayResource> buildFilePart(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "face.jpg";

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.parseMediaType(
                file.getContentType() != null ? file.getContentType() : "image/jpeg"
        ));

        return new HttpEntity<>(resource, fileHeaders);
    }

    private APIException buildFastApiMessageException(String message) {
        if (message == null || message.isBlank()) {
            return new APIException(ErrorCode.FACE_SERVICE_ERROR);
        }

        return new APIException(
                ErrorCode.FACE_SERVICE_ERROR.getStatus(),
                message,
                ErrorCode.FACE_SERVICE_ERROR.getHttpStatusCode()
        );
    }




    private HttpEntity<String> buildTextPart(String value) {
        HttpHeaders textHeaders = new HttpHeaders();
        textHeaders.setContentType(MediaType.TEXT_PLAIN);
        return new HttpEntity<>(value, textHeaders);
    }
}