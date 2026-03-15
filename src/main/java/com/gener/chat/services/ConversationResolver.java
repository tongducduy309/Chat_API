package com.gener.chat.services;

import com.gener.chat.dtos.response.MessageRes;
import com.gener.chat.dtos.response.ReplyMessageRes;
import com.gener.chat.enums.ConversationType;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.mapper.MessageMapper;
import com.gener.chat.models.Conversation;
import com.gener.chat.models.ConversationMember;
import com.gener.chat.models.Message;
import com.gener.chat.repositories.ConversationMemberRepository;
import com.gener.chat.repositories.MessageDeletionRepository;
import com.gener.chat.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConversationResolver {
    private final ConversationMemberRepository memberRepository;
    private final MessageDeletionRepository messageDeletionRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    public String getTitleConversation(Long conversationId, Conversation conversation, Long userId) throws APIException {
        String title = "";

        List<ConversationMember> members = memberRepository.findActiveMember(conversationId);

        if(conversation.getType().equals(ConversationType.DIRECT)){

            return Objects.equals(userId, members.getFirst().getUser().getId()) ? members.getLast().getNickname():members.getFirst().getNickname();

        }

        return conversation.getTitle();

    }

    public MessageRes getLastMessage(Conversation conversation, Long userId) {
        Message globalLast = conversation.getLastMessage();

        if (globalLast == null) {
            return null;
        }

        boolean deletedForUser = messageDeletionRepository
                .existsByIdMessageIdAndIdUserId(globalLast.getId(), userId);

        if (!deletedForUser) {
            return messageMapper.toMessageRes(globalLast,userId);
        }

        Message visibleLast = messageRepository
                .findTopVisibleMessage(conversation.getId(), userId)
                .orElse(null);

        return visibleLast != null ? messageMapper.toMessageRes(visibleLast,userId) : null;
    }

    public Long getSkipMessages(Conversation conversation, Long userId) throws APIException {
        ConversationMember member = memberRepository.findByIdConversationIdAndIdUserId(conversation.getId(),userId).orElseThrow(
                ()-> new APIException(ErrorCode.SENDER_NOT_IN_CONVERSATION));

        if (conversation.getLastMessage()==null||member.getLastReadMessageSeq()==null) return 0L;
        return conversation.getLastMessage().getSeq()-member.getLastReadMessageSeq();
    }


}
