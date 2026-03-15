package com.gener.chat.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter @NoArgsConstructor
@AllArgsConstructor
public class CallParticipantId implements Serializable {
    private Long callId;
    private Long userId;
}
