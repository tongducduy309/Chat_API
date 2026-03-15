package com.gener.chat.dtos.request;

import com.gener.chat.enums.MessageType;
import lombok.Getter;

@Getter
public class SendMessageReq {
    private Long receiverId;
    private Long conversationId;
    private MessageType type;
    private String content;
    private Long replyToId;
}
