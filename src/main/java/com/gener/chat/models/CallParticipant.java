package com.gener.chat.models;

import com.gener.chat.enums.ParticipantState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name="call_participants", indexes = {
        @Index(name="idx_cp_call", columnList="call_id"),
        @Index(name="idx_cp_user", columnList="user_id")
})
@Getter @Setter
public class CallParticipant {
    @EmbeddedId
    private CallParticipantId id;

    @MapsId("callId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="call_id", nullable=false)
    private Call call;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ParticipantState state = ParticipantState.INVITED;

    private Instant joinedAt;
    private Instant leftAt;
}
