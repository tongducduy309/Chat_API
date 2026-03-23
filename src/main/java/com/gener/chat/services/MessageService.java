package com.gener.chat.services;

import com.gener.chat.dtos.request.CreateConversationReq;
import com.gener.chat.dtos.request.SendMessageReq;
import com.gener.chat.dtos.response.MessageRes;
import com.gener.chat.enums.ConversationType;
import com.gener.chat.enums.ErrorCode;
import com.gener.chat.enums.SuccessCode;
import com.gener.chat.exception.APIException;
import com.gener.chat.mapper.MessageMapper;
import com.gener.chat.models.*;
import com.gener.chat.repositories.*;
import com.gener.chat.ws.dto.MessageCreatedEvent;
import com.gener.chat.ws.dto.ReadMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final ApplicationEventPublisher publisher;
    private final UserService userService;
    private final ConversationService conversationService;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageMapper messageMapper;
    private final MessageDeletionRepository messageDeletionRepository;

    @Transactional
    public ResponseEntity<ResponseObject> send(SendMessageReq req) throws APIException {

        User sender = userService.getUserFromToken();
        Conversation conv = null;


        if (req.getConversationId()==null) {
            conv = conversationService.getConversationTogether(sender.getId(),req.getReceiverId(),true);
        }
        else{
            conv = conversationRepo.findByIdForUpdate(req.getConversationId())
                    .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));
        }



        Long maxSeq = messageRepo.maxSeq(conv.getId());
        Long nextSeq = (maxSeq == null ? 1L : maxSeq + 1);

        ConversationMember senderMember = conversationMemberRepository.findByIdConversationIdAndIdUserId(conv.getId(),sender.getId()).orElseThrow(
                ()-> new APIException(ErrorCode.SENDER_NOT_IN_CONVERSATION));

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setSenderMember(senderMember);
        msg.setType(req.getType());
        msg.setContent(req.getContent());
        msg.setSeq(nextSeq);

        if (req.getReplyToId() != null) {
            Message reply = messageRepo.findById(req.getReplyToId())
                    .orElseThrow(() -> new APIException(ErrorCode.REPLY_MESSAGE_NOT_FOUND));
            msg.setReplyTo(reply);
        }

        Message savedMessage = messageRepo.save(msg);

        conv.setLastMessage(msg);
        conversationRepo.save(conv);

        publisher.publishEvent(
                messageMapper.toMessageRes(savedMessage,sender.getId())
        );

        return ResponseEntity.status(SuccessCode.MESSAGE_SENT_SUCCESS.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.MESSAGE_SENT_SUCCESS.getStatus())
                        .message(SuccessCode.MESSAGE_SENT_SUCCESS.getMessage())
                        .data(messageMapper.toMessageRes(savedMessage,sender.getId()))
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getMessage(Long conversationId) {



        return ResponseEntity.status(SuccessCode.MESSAGE_RECIEVED_SUCCESS.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.MESSAGE_RECIEVED_SUCCESS.getStatus())
                        .message(SuccessCode.MESSAGE_RECIEVED_SUCCESS.getMessage())
                        .data(messageRepo.findTop50ByConversationIdOrderBySeqDesc(conversationId))
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> deleteForMe(Long messageId) throws APIException {
        Message message = messageRepo.findById(messageId)
                .orElseThrow(() -> new APIException(ErrorCode.MESSAGE_NOT_FOUND));

        User user = userService.getUserFromToken();

        ConversationMember member = conversationMemberRepository
                .findByIdConversationIdAndIdUserId(message.getConversation().getId(), user.getId())
                .orElseThrow(() -> new APIException(ErrorCode.SENDER_NOT_IN_CONVERSATION));

//        boolean existed = messageDeletionRepository
//                .existsByIdMessageIdAndIdUserId(messageId, userId);
//
//        if (existed) return ;

        MessageDeletion deletion = MessageDeletion.builder()
                .id(new MessageDeletionId(messageId, user.getId()))
                .message(message)
                .user(member.getUser())
                .deletedAt(Instant.now())
                .build();

        MessageDeletion messageDeletion =  messageDeletionRepository.save(deletion);

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Xóa tin nhắn thành công")
                        .data(messageDeletion)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> readMessage(Long conversationId) throws APIException {
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));
        User user = userService.getUserFromToken();

        log.info("1");

        if (conversation.getLastMessage()!=null && !Objects.equals(conversation.getLastMessage().getSender().getId(), user.getId())){

            log.info("2");
            Optional<ConversationMember> conversationMember = conversationMemberRepository.findByIdConversationIdAndIdUserId(conversationId,user.getId());



            if (conversationMember.isPresent())
            {
                conversationMember.get().setLastReadMessageId(conversation.getLastMessage().getId());
                conversationMember.get().setLastReadMessageSeq(conversation.getLastMessage().getSeq());
                conversationMemberRepository.save(conversationMember.get());
                publisher.publishEvent(new ReadMessageEvent(conversation.getLastMessage().getId(),conversationId,conversationMember.get()));
            }

        }



        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(null)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> searchMessages(Long conversationId, String keyword) throws APIException {
        Conversation conversation = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));

        User user = userService.getUserFromToken();

        List<Message> messages = messageRepo.searchMessages(conversationId,user.getId(),keyword);

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message("Kết quả tìm kiếm")
                        .data(messageMapper.toListMessageSearchRes(messages))
                        .build()
        );
    }
}