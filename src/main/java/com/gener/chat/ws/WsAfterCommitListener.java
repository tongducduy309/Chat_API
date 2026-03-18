package com.gener.chat.ws;

import com.gener.chat.dtos.response.CheckInRes;
import com.gener.chat.dtos.response.MessageRes;
import com.gener.chat.handlers.WebSocketHandler;
import com.gener.chat.models.Friendship;
import com.gener.chat.repositories.ConversationMemberRepository;
import com.gener.chat.services.MessageResolver;
import com.gener.chat.ws.dto.ConversationCreatedEvent;
import com.gener.chat.ws.dto.FriendshipEvent;
import com.gener.chat.ws.dto.NameUpdateEvent;
import com.gener.chat.ws.dto.ReadMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class WsAfterCommitListener {
    private final WebSocketHandler ws;
    private final ObjectMapper om;
    private final ConversationMemberRepository memberRepo;
    private final MessageResolver messageResolver;

    private void push(Long userId, String event, Object data) {
        try {
            ws.push(userId, Map.of("event", event, "data", data));
        } catch (Exception ignored) {}
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConversationCreated(ConversationCreatedEvent e) {
        for (Long uid : e.memberIds()) {
            push(uid,"CONVERSATION_CREATE", e);
        }
    }

//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//    public void onMessageCreated(MessageCreatedEvent e) {
//        List<Long> memberIds =
//                memberRepo.findActiveMemberIds(e.conversationId());
//        for (Long userId : memberIds) {
//            if (!Objects.equals(userId, e.senderId()))
//            {
//                ws.push(userId, e, om);
//                log.info("Receiver ID: "+userId);
//            }
//        }
//    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageRes e) {
        List<Long> memberIds =
                memberRepo.findActiveMemberIds(e.getConversationId());
        for (Long userId : memberIds) {
//            ws.push(userId, e);
            e.setReplyTo(messageResolver.getReplyMessage(e.getReplyTo()!=null?e.getReplyTo().getId():null,userId));
            push(userId,"MESSAGE", e);
        }

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReadMessage(ReadMessageEvent e) {
        List<Long> memberIds =
                memberRepo.findActiveMemberIds(e.conversationId());
        for (Long userId : memberIds) {
            if (!Objects.equals(e.member().getUser().getId(), userId))
            {
                push(userId,"READ_MESSAGE", Map.of("messageId",e.messageId(),"member",e.member()));
            }
        }

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNameUpdate(NameUpdateEvent e) {
        List<Long> memberIds =
                memberRepo.findActiveMemberIds(e.conversationId());
        for (Long userId : memberIds) {
            push(userId,"NAME_UPDATE", Map.of("nameUpdateRes",Map.of("conversationId",e.conversationId()),"messageRes",e.messageRes()));
        }

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCheckIn(CheckInRes e) {
        push(e.getUserId(),"ATTENDANCE_CHECK_IN", e);

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFriendship(Friendship friendship) {
        push(friendship.getUser().getId(), "FRIENDSHIP_UPDATE", Map.of("friendshipStatus",friendship.getStatusRes(friendship.getUser().getId()),"targetUserId",friendship.getPeer().getId()));
        push(friendship.getPeer().getId(), "FRIENDSHIP_UPDATE", Map.of("friendshipStatus",friendship.getStatusRes(friendship.getPeer().getId()),"targetUserId",friendship.getUser().getId()));
    }

}
