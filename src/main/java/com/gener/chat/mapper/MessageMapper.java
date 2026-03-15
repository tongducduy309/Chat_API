package com.gener.chat.mapper;

import com.gener.chat.dtos.response.MessageRes;
import com.gener.chat.dtos.response.MessageSearchRes;
import com.gener.chat.dtos.response.ReplyMessageRes;
import com.gener.chat.models.Message;
import com.gener.chat.services.ConversationResolver;
import com.gener.chat.services.MessageResolver;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class MessageMapper {
    @Autowired
    protected MessageResolver messageResolver;

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderNickname", source = "senderMember.nickname")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "replyTo", expression = "java(messageResolver.getReplyMessage(message.getReplyTo()!=null?message.getReplyTo().getId():null, userId))")
    public abstract MessageRes toMessageRes(Message message, @Context Long userId);

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderNickname", source = "senderMember.nickname")
    @Mapping(target = "createdAt", source = "createdAt")
    public abstract ReplyMessageRes toReplyMessageRes(Message message);


    public abstract MessageSearchRes toMessageSearchRes(Message message);

    public abstract List<MessageSearchRes> toListMessageSearchRes(List<Message> message);


    public abstract List<MessageRes> toListMessageRes(List<Message> messages, @Context Long userId);
}
