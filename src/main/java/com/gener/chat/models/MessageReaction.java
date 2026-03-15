package com.gener.chat.models;


import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
class MessageReactionId implements Serializable {
    private Long messageId;
    private Long userId;
    private String reaction;

    public MessageReactionId() {}
    public MessageReactionId(Long messageId, Long userId, String reaction) {
        this.messageId = messageId; this.userId = userId; this.reaction = reaction;
    }
    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof MessageReactionId that)) return false;
        return Objects.equals(messageId, that.messageId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(reaction, that.reaction);
    }
    @Override public int hashCode(){ return Objects.hash(messageId, userId, reaction); }
}

@Entity
@Table(name="message_reactions",
        indexes = {@Index(name="idx_react_message", columnList="message_id")}
)
public class MessageReaction extends BaseEntity {
    @EmbeddedId
    private MessageReactionId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id", nullable=false)
    private Message message;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="reaction", insertable=false, updatable=false)
    private String reaction;
}
