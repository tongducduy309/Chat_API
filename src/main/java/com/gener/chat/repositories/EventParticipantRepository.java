package com.gener.chat.repositories;

import com.gener.chat.models.EventParticipant;
import com.gener.chat.models.EventParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventParticipantRepository extends JpaRepository<EventParticipant, EventParticipantId> {

    List<EventParticipant> findByUserId(Long userId);

    List<EventParticipant> findByEventId(Long eventId);
}