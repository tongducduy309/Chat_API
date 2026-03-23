package com.gener.chat.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.CallStatus;
import com.gener.chat.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name="messages",
        indexes = {
                @Index(name="idx_msg_conv_seq", columnList="conversation_id, seq"),
                @Index(name="idx_msg_conv_time", columnList="conversation_id, created_at"),
                @Index(name="idx_msg_sender", columnList="sender_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name="uk_msg_conv_seq", columnNames={"conversation_id","seq"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="conversation_id", nullable=false)
    @JsonBackReference
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "conversation_member_id", referencedColumnName = "conversation_id", nullable = false),
            @JoinColumn(name = "sender_member_id", referencedColumnName = "user_id", nullable = false)
    })
    private ConversationMember senderMember;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private MessageType type = MessageType.TEXT;

    @Column(columnDefinition="text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reply_to_id")
    private Message replyTo;

    @Column(nullable=false)
    private Long seq;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime editedAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "call_id", unique = true)
    private Call call;

    @PrePersist
    public void prePersist() { if (createdAt==null) this.createdAt = LocalDateTime.now(); }
}

