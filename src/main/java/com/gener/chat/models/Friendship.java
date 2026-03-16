package com.gener.chat.models;

import com.gener.chat.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
@Entity
@Table(name="friendships",
        indexes = {@Index(name="idx_friend_status", columnList="status")}
)
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
@Builder
public class Friendship extends BaseEntity {
    @EmbeddedId
    private FriendshipId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @MapsId("peerId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="peer_id", nullable=false)
    private User peer;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="requested_by", nullable=false)
    private User requestedBy;
}
