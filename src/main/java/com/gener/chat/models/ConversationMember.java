package com.gener.chat.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gener.chat.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="conversation_members",
        indexes = {
                @Index(name="idx_members_user", columnList="user_id"),
                @Index(name="idx_members_conv", columnList="conversation_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMember {
    @EmbeddedId
    private ConversationMemberId id;

    @MapsId("conversationId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="conversation_id", nullable=false)
    @JsonIgnore
    private Conversation conversation;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="add_by_user_id", nullable=false)
    private User addByUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private MemberRole role = MemberRole.MEMBER;

    @Column(length=100)
    private String nickname;

    private String passwordConversation;

    @Column(nullable=false)
    private Instant joinedAt = Instant.now();

    private Instant leftAt;

    @Column(nullable=false)
    private boolean isMuted = false;

    private Instant mutedUntil;

    private Long lastReadMessageId;

    private Long lastReadMessageSeq;

}
