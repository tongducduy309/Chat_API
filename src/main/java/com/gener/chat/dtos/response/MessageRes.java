package com.gener.chat.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.MessageType;
import com.gener.chat.models.ConversationMember;
import com.gener.chat.models.Message;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRes {
    private Long id;
    private Long conversationId;
    private MessageType type;
    private String content;
    private Long seq;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private String senderNickname;
    private Long senderId;
    private ReplyMessageRes replyTo;
}
