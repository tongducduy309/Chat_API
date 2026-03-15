package com.gener.chat.models;


import com.gener.chat.enums.AttachmentType;
import jakarta.persistence.*;

@Entity
@Table(name="attachments", indexes = {
        @Index(name="idx_att_message", columnList="message_id")
})
public class Attachment extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="message_id", nullable=false)
    private Message message;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private AttachmentType type;

    @Column(nullable=false, length=1024)
    private String url;

    @Column(length=100)
    private String mimeType;

    @Column(length=255)
    private String fileName;

    private Long fileSize;
    private Integer width;
    private Integer height;
    private Integer durationMs;
}
