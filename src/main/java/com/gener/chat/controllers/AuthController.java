package com.gener.chat.controllers;

import com.gener.chat.dtos.request.IntrospectReq;
import com.gener.chat.dtos.request.LoginReq;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    ResponseEntity<ResponseObject> authenticate(@RequestBody LoginReq loginReq) throws APIException {
        return authService.login(loginReq);
    }

    @PostMapping("/introspect")
    ResponseEntity<ResponseObject> introspect(@RequestBody IntrospectReq introspectRequest) throws APIException {
        return authService.introspect(introspectRequest);
    }

    @PostMapping(value = "/face-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> faceLogin(@RequestPart("file") MultipartFile file) throws APIException {
        return authService.faceLogin(file);
    }
}
