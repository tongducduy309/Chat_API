package com.gener.chat.models;

import com.gener.chat.enums.CallStatus;
import com.gener.chat.enums.CallType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="calls", indexes = {
        @Index(name="idx_calls_conv", columnList="conversation_id"),
        @Index(name="idx_calls_caller", columnList="caller_id"),
        @Index(name="idx_calls_status", columnList="status")
})
@Getter
@Setter
public class Call {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="conversation_id", nullable=false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="caller_id", nullable=false)
    private User caller;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private CallType type = CallType.VOICE;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private CallStatus status = CallStatus.RINGING;

    private Instant startedAt;
    private Instant endedAt;

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();
}
