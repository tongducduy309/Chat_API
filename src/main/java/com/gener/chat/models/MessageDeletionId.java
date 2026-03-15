package com.gener.chat.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MessageDeletionId implements Serializable {
    private Long messageId;
    private Long userId;
}
