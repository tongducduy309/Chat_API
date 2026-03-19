package com.gener.chat.repositories;

import com.gener.chat.models.FaceProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FaceProfileRepository extends JpaRepository<FaceProfile, Long> {
    Optional<FaceProfile> findByUserId(Long userId);
    boolean existsByUserIdAndRegisteredTrue(Long userId);
    List<FaceProfile> findAllByRegisteredTrue();
}