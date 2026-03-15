package com.gener.chat.ws.dto;

public record IncomingCallCallerEvent(Long callId, Long conversationId, Long callerId, Long receiverId, String title, String type) {}

