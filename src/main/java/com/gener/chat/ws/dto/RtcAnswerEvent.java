package com.gener.chat.ws.dto;

public record RtcAnswerEvent(Long callId, Long fromUserId, Long toUserId, String sdp) {}
