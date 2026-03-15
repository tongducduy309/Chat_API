package com.gener.chat.repositories;

import com.gener.chat.models.CallParticipant;
import com.gener.chat.models.CallParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CallParticipantRepository extends JpaRepository<CallParticipant, CallParticipantId> {
    @Query("""
    select cp.user.id from CallParticipant cp
    where cp.call.id = :callId
  """)
    List<Long> findUserIds(@Param("callId") Long callId);

    @Query("""
    select cp from CallParticipant cp
    where cp.call.id = :callId and cp.user.id = :userId
  """)
    Optional<CallParticipant> findOne(@Param("callId") Long callId, @Param("userId") Long userId);
}
