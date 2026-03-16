package com.gener.chat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_attendance_user_work_date", columnNames = {"user_id", "work_date"})
        },
        indexes = {
                @Index(name = "idx_attendance_user_work_date", columnList = "user_id, work_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "work_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate workDate;

    @Column(name = "check_in_at", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime checkInAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 255)
    private String note;
}