package com.gener.chat.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message_deletions",
        indexes = {
                @Index(name = "idx_msg_del_user", columnList = "user_id"),
                @Index(name = "idx_msg_del_msg", columnList = "message_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDeletion {

    @EmbeddedId
    private MessageDeletionId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant deletedAt;
}

