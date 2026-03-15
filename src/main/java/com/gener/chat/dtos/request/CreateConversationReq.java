package com.gener.chat.dtos.request;

import com.gener.chat.enums.ConversationType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationReq {
    private ConversationType conversationType;
    private String title;
    private List<Long> memberIds;
}
