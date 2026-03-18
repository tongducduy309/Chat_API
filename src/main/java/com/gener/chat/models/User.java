package com.gener.chat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name="idx_users_phone", columnList="phone"),
                @Index(name="idx_users_email", columnList="email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name="uk_users_phone", columnNames="phone"),
                @UniqueConstraint(name="uk_users_email", columnNames="email")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 8)
    private String userCode;

    @Column(unique = true,nullable = false,length = 10)
    private String phone;

    @Column(unique = true,nullable = false)
    private String email;

    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(length = 512)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "hire_date")
    private LocalDate hireDate;

}
