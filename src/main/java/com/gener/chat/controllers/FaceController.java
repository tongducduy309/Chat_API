package com.gener.chat.controllers;

import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.FaceProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/face")
@RequiredArgsConstructor
public class FaceController {

    private final FaceProfileService faceProfileService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> register(@RequestPart("file") MultipartFile file) throws APIException {
        return faceProfileService.register(file);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseObject> me() throws APIException {
        return faceProfileService.me();
    }
}