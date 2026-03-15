package com.gener.chat.ws.dto;

import java.time.Instant;
import java.util.List;

public record MessageCreatedEvent(
        Long conversationId,
        Long messageId,
        Long senderId,
        String type,
        String content,
        Long seq,
        Instant createdAt
) {}
