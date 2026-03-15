package com.gener.chat.ws.dto;

import com.gener.chat.models.ConversationMember;


public record ReadMessageEvent(Long messageId, Long conversationId, ConversationMember member) {}
