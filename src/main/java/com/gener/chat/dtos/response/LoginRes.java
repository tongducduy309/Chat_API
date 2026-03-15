package com.gener.chat.dtos.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRes {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private Instant expiresAt;
}
