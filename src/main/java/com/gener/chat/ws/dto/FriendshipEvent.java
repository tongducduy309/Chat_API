package com.gener.chat.ws.dto;

import com.gener.chat.models.Friendship;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
public class FriendshipEvent {
    private Friendship friendship;
}
