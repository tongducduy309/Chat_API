package com.gener.chat.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceSearchResult {
    private Boolean matched;
    private Long userId;
    private Double distance;
    private Double threshold;
    private String model;
}