package com.gener.chat.dtos.response;

import java.time.Instant;

public record CallRes(Long callId, Long conversationId, Long callerId, Long receiverId,
                      String status, String type, Instant createdAt) {}
