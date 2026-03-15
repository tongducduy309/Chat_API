package com.gener.chat.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ConversationMemberId implements Serializable {
    private Long conversationId;
    private Long userId;

    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof ConversationMemberId that)) return false;
        return Objects.equals(conversationId, that.conversationId)
                && Objects.equals(userId, that.userId);
    }
    @Override public int hashCode(){ return Objects.hash(conversationId, userId); }
}
