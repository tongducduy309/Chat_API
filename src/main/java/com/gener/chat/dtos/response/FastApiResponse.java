package com.gener.chat.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FastApiResponse<T> {
    private Boolean success;
    private String message;
    private T data;
}