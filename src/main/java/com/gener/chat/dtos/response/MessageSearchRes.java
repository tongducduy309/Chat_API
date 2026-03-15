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
public class MessageSearchRes {
    private Long id;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;
    private String content;
}
