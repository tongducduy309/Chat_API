package com.gener.chat.dtos.response;

import com.gener.chat.enums.ConversationType;
import com.gener.chat.enums.FriendshipStatus;
import com.gener.chat.enums.MemberRole;
import com.gener.chat.models.ConversationMember;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Builder
public class DetailConversationRes {
    private String title;
    private String avatarUrl;
    private ConversationType type;
    private List<MessageRes> messages;
    private List<ConversationMember> members;
    private MemberRole role;
    private Long creatorId;
    private FriendshipStatus friendshipStatus;
    private Long targetUserId;
}
