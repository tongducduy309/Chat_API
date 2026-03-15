package com.gener.chat.ws.dto;

public record IncomingCallReceiverEvent(Long callId, Long conversationId, Long callerId, Long receiverId, String title, String type) {}
