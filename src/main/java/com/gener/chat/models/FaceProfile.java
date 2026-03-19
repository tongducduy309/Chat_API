package com.gener.chat.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "face_profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_face_profiles_user", columnNames = "user_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaceProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String embedding;

    @Column(nullable = false)
    private Boolean registered;
}