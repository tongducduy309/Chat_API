package com.gener.chat.dtos.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceSearchCandidate {
    private Long userId;
    private List<Double> embedding;
}