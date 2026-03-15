package com.gener.chat.controllers;

import com.gener.chat.dtos.request.IntrospectReq;
import com.gener.chat.dtos.request.LoginReq;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.AuthService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
