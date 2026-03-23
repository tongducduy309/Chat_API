package com.gener.chat.models;

import com.gener.chat.enums.EventParticipantStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "event_participants", indexes = {
        @Index(name = "idx_event_participant_user", columnList = "user_id"),
        @Index(name = "idx_event_participant_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventParticipant {

    @EmbeddedId
    private EventParticipantId id;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventParticipantStatus status = EventParticipantStatus.PENDING;
}