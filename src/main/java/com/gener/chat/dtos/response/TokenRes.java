package com.gener.chat.dtos.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRes {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private Instant expiresAt;
}
