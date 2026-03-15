package com.gener.chat.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateUserReq {
    private String phone;
    private String email;
    private String displayName;
    private String password;
}
