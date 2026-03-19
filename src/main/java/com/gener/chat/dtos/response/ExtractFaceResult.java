package com.gener.chat.dtos.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtractFaceResult {
    private List<Double> embedding;
    private String model;
}