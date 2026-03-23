package com.gener.chat.controllers;

import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.DepartmentService;
import com.gener.chat.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/departments/employees")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping()
    ResponseEntity<ResponseObject> getAllEmployees(){
        return departmentService.getAllEmployees();
    }
}