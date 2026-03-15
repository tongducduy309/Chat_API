package com.gener.chat.dtos.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameUpdateReq {
    private Long userId;
    private String name;

}
