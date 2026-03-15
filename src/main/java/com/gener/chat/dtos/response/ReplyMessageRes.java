package com.gener.chat.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gener.chat.enums.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyMessageRes {
    private Long id;
    private Long conversationId;
    private MessageType type;
    private String content;
    private Long seq;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private String senderNickname;
    private Long senderId;
}
