package com.gener.chat.ws.dto;

import com.gener.chat.dtos.response.ConversationRes;

import java.time.Instant;
import java.util.List;

public record ConversationCreatedEvent(
        Long conversationId,
        List<Long> memberIds
) {}