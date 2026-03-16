package com.gener.chat.dtos.response;

import com.gener.chat.enums.FriendshipStatus;
import com.gener.chat.models.FriendshipId;
import com.gener.chat.models.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSearchRes {
    private Long id;
    private String displayName;
    private String avatarUrl;
    private String phone;
    private String userCode;
    private FriendshipStatus status;
    private Long requestedById;
}
