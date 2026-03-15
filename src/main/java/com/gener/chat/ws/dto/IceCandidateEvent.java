package com.gener.chat.ws.dto;

public record IceCandidateEvent(Long callId, Long fromUserId, Long toUserId, String candidate, String sdpMid, Integer sdpMLineIndex) {}
