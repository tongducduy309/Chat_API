package com.gener.chat.models;

import com.gener.chat.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("EVENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventMessage extends Message {

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 30)
    private EventType eventType;

    @Column(name = "event_payload", columnDefinition = "text")
    private String eventPayload;
}
