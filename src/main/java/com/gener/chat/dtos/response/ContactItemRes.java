package com.gener.chat.dtos.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContactItemRes {
    private Long id;
    private String displayName;
    private String avatarUrl;
}
