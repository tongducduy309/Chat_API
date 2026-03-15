package com.gener.chat.ws.dto;

public record RtcOfferEvent(Long callId, Long fromUserId, Long toUserId, String sdp) {}
