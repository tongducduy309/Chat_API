package com.gener.chat.services;

import com.gener.chat.dtos.response.MessageRes;
import com.gener.chat.dtos.response.ReplyMessageRes;
import com.gener.chat.enums.ConversationType;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageResolver {
    private final MessageDeletionRepository messageDeletionRepository;
    private final MessageRepository messageRepository;


    @Transactional
    public ReplyMessageRes getReplyMessage(Long replyMessageToId, Long userId) {
        if (replyMessageToId==null) return null;


        boolean deletedForUser = messageDeletionRepository
                .existsByIdMessageIdAndIdUserId(replyMessageToId, userId);

        Optional<Message> mes = messageRepository.findById(replyMessageToId);
        Message message = mes.get();
        if (deletedForUser) {
            return ReplyMessageRes.builder()
                    .id(replyMessageToId)
                    .senderNickname(message.getSenderMember().getNickname())
                    .seq(message.getSeq())
                    .type(message.getType())
                    .content("Tin nhắn đã bị xóa")
                    .conversationId(message.getConversation().getId())
                    .createdAt(message.getCreatedAt())
                    .senderId(message.getSender().getId())
                    .build();
        }

        return ReplyMessageRes.builder()
                .id(replyMessageToId)
                .senderNickname(message.getSenderMember().getNickname())
                .seq(message.getSeq())
                .type(message.getType())
                .content(message.getContent())
                .conversationId(message.getConversation().getId())
                .createdAt(message.getCreatedAt())
                .senderId(message.getSender().getId())
                .build();

    }


}
