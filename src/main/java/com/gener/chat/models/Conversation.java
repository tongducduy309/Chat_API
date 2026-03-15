package com.gener.chat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gener.chat.enums.ConversationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversations",
        indexes = {@Index(name="idx_conv_type", columnList="type")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Conversation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationType type;
    @Column(length = 150)
    private String title;
    @Column(length = 512)
    private String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="created_by")
    private User createdBy;

    @JsonManagedReference
    @OneToMany(mappedBy = "conversation")
    private List<Message> messages;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="last_message_id")
    private Message lastMessage;




}
