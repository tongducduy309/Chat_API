package com.gener.chat.repositories;

import com.gener.chat.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCreatorIdOrderByStartTimeAsc(Long creatorId);

    List<Event> findByCreatorIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long creatorId,
            LocalDateTime start,
            LocalDateTime end
    );
}