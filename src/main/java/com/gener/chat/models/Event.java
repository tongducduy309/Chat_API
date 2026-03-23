package com.gener.chat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.EventLevel;
import com.gener.chat.enums.EventStatus;
import com.gener.chat.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_creator", columnList = "creator_id"),
        @Index(name = "idx_event_start_time", columnList = "start_time"),
        @Index(name = "idx_event_end_time", columnList = "end_time"),
        @Index(name = "idx_event_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

//    @Column(name = "location", length = 255)
//    private String location;

//    @Column(name = "color", length = 50)
//    private String color;

//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 30)
//    private EventType type = EventType.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventLevel type = EventLevel.PERSONAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventStatus status = EventStatus.UPCOMING;

//    @Column(name = "all_day", nullable = false)
//    private Boolean allDay = false;

//    @Column(name = "is_recurring", nullable = false)
//    private Boolean recurring = false;

//    @Column(name = "recurrence_rule", length = 255)
//    private String recurrenceRule;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
//    @Column(name = "reminder_time")
//    private LocalDateTime reminderTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventParticipant> participants = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}