package com.gener.chat.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    void preUpdate() { this.updatedAt = LocalDateTime.now(); }

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
