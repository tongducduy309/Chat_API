package com.gener.chat.controllers;

import com.gener.chat.exception.APIException;
import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    ResponseEntity<ResponseObject> getProfile() throws APIException {
        return userService.getProfile();
    }



}
