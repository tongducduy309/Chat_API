package com.gener.chat.models;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FriendshipId implements Serializable {
    private Long userId;
    private Long peerId;

    public FriendshipId() {}
    public FriendshipId(Long userId, Long peerId) { this.userId = userId; this.peerId = peerId; }

    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof FriendshipId that)) return false;
        return Objects.equals(userId, that.userId) && Objects.equals(peerId, that.peerId);
    }
    @Override public int hashCode(){ return Objects.hash(userId, peerId); }
}
