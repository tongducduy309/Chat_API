package com.gener.chat.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceVerifyResult {
    private Boolean matched;
    private Double distance;
    private Double threshold;
    private String model;
}