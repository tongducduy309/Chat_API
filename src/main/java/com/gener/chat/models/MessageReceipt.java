package com.gener.chat.models;


import com.gener.chat.enums.ReceiptStatus;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Embeddable
class MessageReceiptId implements Serializable {
    private Long messageId;
    private Long userId;

    public MessageReceiptId() {}
    public MessageReceiptId(Long messageId, Long userId) {
        this.messageId = messageId; this.userId = userId;
    }
    @Override public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof MessageReceiptId that)) return false;
        return Objects.equals(messageId, that.messageId) && Objects.equals(userId, that.userId);
    }
    @Override public int hashCode(){ return Objects.hash(messageId, userId); }
}

@Entity
@Table(name="message_receipts",
        indexes = {@Index(name="idx_receipt_user", columnList="user_id, updated_at")}
)
public class MessageReceipt {
    @EmbeddedId
    private MessageReceiptId id;

    @MapsId("messageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id", nullable=false)
    private Message message;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ReceiptStatus status;

    @Column(nullable=false)
    private Instant updatedAt = Instant.now();

    @PreUpdate void preUpdate(){ this.updatedAt = Instant.now(); }
}
