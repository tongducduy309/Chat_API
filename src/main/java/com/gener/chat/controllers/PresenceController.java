package com.gener.chat.controllers;

import com.gener.chat.models.ResponseObject;
import com.gener.chat.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseObject> isOnline(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .status(200)
                        .message("Trạng thái online")
                        .data(presenceService.isOnline(userId))
                        .build()
        );
    }
}