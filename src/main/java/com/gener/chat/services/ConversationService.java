package com.gener.chat.services;

import com.gener.chat.dtos.request.CreateConversationReq;
import com.gener.chat.dtos.request.NameUpdateReq;
import com.gener.chat.dtos.response.DetailConversationRes;
import com.gener.chat.enums.*;
import com.gener.chat.exception.APIException;
import com.gener.chat.mapper.ConversationMapper;
import com.gener.chat.mapper.MessageMapper;
import com.gener.chat.models.*;
import com.gener.chat.repositories.ConversationMemberRepository;
import com.gener.chat.repositories.ConversationRepository;
import com.gener.chat.repositories.MessageRepository;
import com.gener.chat.repositories.UserRepository;
import com.gener.chat.ws.dto.ConversationCreatedEvent;
import com.gener.chat.ws.dto.ReadMessageEvent;
import com.gener.chat.ws.dto.NameUpdateEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConversationService {
    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationMemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;
    private final UserService userService;
    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final ConversationResolver conversationResolver;
    private final MessageRepository messageRepository;

    @Transactional
    public ResponseEntity<ResponseObject> createConversation(CreateConversationReq req, Long creatorId) throws APIException {

        if (req.getConversationType()==ConversationType.DIRECT){
            Optional<Conversation> c = conversationRepository.findDirectConversation(req.getMemberIds().getFirst(),req.getMemberIds().getLast());
            if (c.isPresent()){
                throw new APIException(ErrorCode.DIRECT_CONVERSATION_ALREADY_EXISTS);
            }
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

        Conversation conversation = Conversation.builder()
                .type(req.getConversationType())
                .title(req.getTitle())
                .createdBy(creator)
        .build();

        Conversation saved = conversationRepository.save(conversation);


        for (Long userId : req.getMemberIds()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_FOUND));

            ConversationMember member = new ConversationMember();
            member.setId(new ConversationMemberId(conversation.getId(), user.getId()));
            member.setConversation(conversation);
            member.setUser(user);
            member.setNickname(user.getDisplayName());
            member.setAddByUser(creator);

            if (user.getId().equals(creatorId)) {
                member.setRole(MemberRole.LEADER);
            } else {
                member.setRole(MemberRole.MEMBER);
            }

            memberRepository.save(member);
        }

        publisher.publishEvent(new ConversationCreatedEvent(
                saved.getId(),
                req.getMemberIds()
        ));

        return ResponseEntity.status(SuccessCode.CONVERSATION_CREATED.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.CONVERSATION_CREATED.getStatus())
                        .message(SuccessCode.CONVERSATION_CREATED.getMessage())
                        .data(saved)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getListConversationsByUserId() throws APIException{
        User user = userService.getUserFromToken();

        List<Conversation> conversations = conversationRepository.findAllByUserId(user.getId());

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(conversationMapper.toListConversationRes(conversations,user.getId()))
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> searchConversationsByUserId(String keyword) throws APIException{
        User user = userService.getUserFromToken();

        List<Conversation> conversations = conversationRepository.searchConversationsByUserIdAndKeyword(user.getId(),keyword);

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(conversationMapper.toListConversationRes(conversations,user.getId()))
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getDetailConversationById(Long conversationId) throws APIException {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));

        List<ConversationMember> members = memberRepository.findActiveMember(conversationId);

        User user = userService.getUserFromToken();
        String title = conversationResolver.getTitleConversation(conversationId,conversation, user.getId());

        List<Message> messages = messageRepository.findVisibleMessages(conversationId,user.getId());

        ConversationMember conversationMember = memberRepository.findByIdConversationIdAndIdUserId(conversationId,user.getId()).orElseThrow(
                ()-> new APIException(ErrorCode.USER_NOT_IN_CONVERSATION)
        );
        DetailConversationRes detailConversationRes = DetailConversationRes.builder()
                .avatarUrl(conversation.getAvatarUrl())
                .title(title)
                .members(members)
                .type(conversation.getType())
                .role(conversationMember.getRole())
                .messages(messageMapper.toListMessageRes(messages,user.getId()))
                .creatorId(conversation.getCreatedBy().getId())
                .build();

        if (conversation.getLastMessage()!=null&& !Objects.equals(conversation.getLastMessage().getSender().getId(), user.getId())){


            conversationMember.setLastReadMessageId(conversation.getLastMessage().getId());
            conversationMember.setLastReadMessageSeq(conversation.getLastMessage().getSeq());
            memberRepository.save(conversationMember);
            publisher.publishEvent(new ReadMessageEvent(conversation.getLastMessage().getId(),conversationId,conversationMember));
        }

        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(detailConversationRes)
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> getConversationById(Long conversationId) throws APIException {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));

        User user = userService.getUserFromToken();


        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(conversationMapper.toConversationRes(conversation,user.getId()))
                        .build()
        );
    }



    @Transactional
    public ResponseEntity<ResponseObject> updateNickname(Long conversationId, NameUpdateReq req) throws APIException {


        User userChangeNickname = userRepository.findById(req.getUserId())
                .orElseThrow(()-> new APIException(ErrorCode.USER_NOT_FOUND));

        ConversationMember conversationMember = memberRepository.findByIdConversationIdAndIdUserId(conversationId,req.getUserId())
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_IN_CONVERSATION));

        conversationMember.setNickname(req.getName());

        User user = userService.getUserFromToken();
        sendMessage(conversationId,"Biệt danh của "+ userChangeNickname.getDisplayName() + " đã được đổi thành "+ req.getName()+" bởi "+ user.getDisplayName(), MessageType.SYSTEM);


        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(memberRepository.save(conversationMember))
                        .build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseObject> updateTitleGroup(Long conversationId, NameUpdateReq req) throws APIException {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));

        conversation.setTitle(req.getName());

        User user = userService.getUserFromToken();


        sendMessage(conversationId,"Tên nhóm đã được đổi thành "+ req.getName()+" bởi "+ user.getDisplayName(), MessageType.SYSTEM);


        return ResponseEntity.status(SuccessCode.REQUEST.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REQUEST.getStatus())
                        .message(SuccessCode.REQUEST.getMessage())
                        .data(conversationMapper.toConversationRes(conversationRepository.save(conversation), user.getId()))
                        .build()
        );
    }

    public ResponseEntity<ResponseObject> removeUserFromConversation(Long conversationId, Long userId) throws APIException {


        ConversationMember conversationMember = memberRepository.findByIdConversationIdAndIdUserId(conversationId,userId)
                .orElseThrow(() -> new APIException(ErrorCode.USER_NOT_IN_CONVERSATION));

        conversationMember.setLeftAt(Instant.now());

        memberRepository.save(conversationMember);

        sendMessage(conversationId, conversationMember.getUser().getDisplayName()+" đã bị xóa khỏi nhóm", MessageType.SYSTEM);

        return ResponseEntity.status(SuccessCode.REMOVE_USER_FROM_CONVERSATION_SUCCESS.getHttpStatusCode()).body(
                ResponseObject.builder()
                        .status(SuccessCode.REMOVE_USER_FROM_CONVERSATION_SUCCESS.getStatus())
                        .message(SuccessCode.REMOVE_USER_FROM_CONVERSATION_SUCCESS.getMessage())
                        .build()
        );
    }




    public Conversation getConversationTogether(Long userId1, Long userId2) throws APIException {
        Optional<Conversation> conversation = conversationRepository.findDirectConversation(userId1,userId2);
        if (conversation.isPresent()){
            return conversation.get();
        }

        CreateConversationReq conversationReq = CreateConversationReq.builder()
                .conversationType(ConversationType.DIRECT)
                .memberIds(List.of(userId1,userId2))
                .build();
        ResponseEntity<ResponseObject> res = createConversation(conversationReq,userId1);
        assert res.getBody() != null;
        return (Conversation) res.getBody().getData();
    }

    public void sendMessage(Long conversationId, String content, MessageType type) throws APIException {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new APIException(ErrorCode.CONVERSATION_NOT_FOUND));

        Long maxSeq = messageRepository.maxSeq(conversationId);
        Long nextSeq = (maxSeq == null ? 1L : maxSeq + 1);

        User user = userService.getUserFromToken();


        ConversationMember senderMember = memberRepository.findByIdConversationIdAndIdUserId(conversationId,user.getId()).orElseThrow(
                ()-> new APIException(ErrorCode.SENDER_NOT_IN_CONVERSATION));

        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setSender(user);
        msg.setSenderMember(senderMember);
        msg.setType(type);
        msg.setContent(content);
        msg.setSeq(nextSeq);
        msg.setReplyTo(null);

        Message savedMessage = messageRepository.save(msg);

        conversation.setLastMessage(msg);
        conversationRepository.save(conversation);

        publisher.publishEvent(new NameUpdateEvent(conversationId,messageMapper.toMessageRes(savedMessage,user.getId())));
    }


}
