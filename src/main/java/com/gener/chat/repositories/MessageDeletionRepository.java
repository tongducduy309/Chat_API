package com.gener.chat.repositories;

import com.gener.chat.models.Message;
import com.gener.chat.models.MessageDeletion;
import com.gener.chat.models.MessageDeletionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MessageDeletionRepository extends JpaRepository<MessageDeletion, MessageDeletionId> {
    boolean existsByIdMessageIdAndIdUserId(Long messageId, Long userId);
}
