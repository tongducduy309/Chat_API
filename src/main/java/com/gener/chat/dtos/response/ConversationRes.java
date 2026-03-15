package com.gener.chat.dtos.response;

import com.gener.chat.models.ConversationMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class ConversationRes {
    private Long id;
    private String title;
    private String avatarUrl;
    private MessageRes lastMessage;
    private Long skipMessages;
}
