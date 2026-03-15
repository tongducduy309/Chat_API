package com.gener.chat.ws.dto;

import com.gener.chat.dtos.response.MessageRes;

public record NameUpdateEvent(Long conversationId, MessageRes messageRes) {}
