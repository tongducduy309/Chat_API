package com.gener.chat.mapper;

import com.gener.chat.dtos.response.ConversationRes;
import com.gener.chat.exception.APIException;
import com.gener.chat.models.Conversation;
import com.gener.chat.services.ConversationResolver;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ConversationMapper {

    @Autowired
    protected ConversationResolver conversationResolver;

    @Mapping(target = "title",
            expression = "java(conversationResolver.getTitleConversation(conversation.getId(), conversation, userId))")
    @Mapping(target = "avatarUrl", source = "conversation.avatarUrl")
    @Mapping(target = "id", source = "conversation.id")
    @Mapping(target = "lastMessage", expression = "java(conversationResolver.getLastMessage(conversation, userId))")
    @Mapping(target = "skipMessages", expression = "java(conversationResolver.getSkipMessages(conversation, userId))")
    public abstract ConversationRes toConversationRes(Conversation conversation, @Context Long userId) throws APIException;

    public abstract List<ConversationRes> toListConversationRes(List<Conversation> conversations, @Context Long userId) throws APIException;
}
