package com.gener.chat.ws.dto;

public record CallEndedEvent(Long callId, Long callerId, Long duration) {}

