package com.gener.chat.services;

import com.gener.chat.dtos.request.CreateCallReq;
import com.gener.chat.dtos.response.CallRes;
import com.gener.chat.enums.*;
import com.gener.chat.exception.APIException;
import com.gener.chat.mapper.MessageMapper;
import com.gener.chat.models.*;
import com.gener.chat.repositories.*;
import com.gener.chat.ws.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallService {
    private final CallRepository callRepo;
    private final CallParticipantRepository cpRepo;
    private final ConversationRepository conversationRepo;
    private final UserRepository userRepo;
    private final ApplicationEventPublisher publisher;
    private final MessageRepository messageRepo;
    private final ConversationMemberRepository conversationMemberRepository;
    private final MessageMapper messageMapper;
    private final ConversationResolver conversationResolver;

    @Transactional
    public CallRes create(CreateCallReq req) throws APIException {
        Conversation conv = conversationRepo.findById(req.conversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        User caller = userRepo.findById(req.callerId())
                .orElseThrow(() -> new RuntimeException("Caller not found"));
        User receiver = userRepo.findById(req.receiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        // TODO: check conv.type == DIRECT và cả 2 thuộc conversation_members

        Call call = new Call();
        call.setConversation(conv);
        call.setCaller(caller);
        call.setType(CallType.VOICE);
        call.setStatus(CallStatus.RINGING);
        callRepo.save(call);

        CallParticipant p1 = new CallParticipant();
        p1.setId(new CallParticipantId(call.getId(), caller.getId()));
        p1.setCall(call);
        p1.setUser(caller);
        p1.setState(ParticipantState.ACCEPTED);
        p1.setJoinedAt(Instant.now());
        cpRepo.save(p1);

        CallParticipant p2 = new CallParticipant();
        p2.setId(new CallParticipantId(call.getId(), receiver.getId()));
        p2.setCall(call);
        p2.setUser(receiver);
        p2.setState(ParticipantState.INVITED);
        cpRepo.save(p2);

        // publish incoming call AFTER_COMMIT
        publisher.publishEvent(new IncomingCallCallerEvent(call.getId(), conv.getId(), caller.getId(), receiver.getId(), conversationResolver.getTitleConversation(conv.getId(), conv, receiver.getId()), "VOICE"));
        publisher.publishEvent(new IncomingCallReceiverEvent(call.getId(), conv.getId(), caller.getId(), receiver.getId(), conversationResolver.getTitleConversation(conv.getId(), conv, caller.getId()), "VOICE"));

        return new CallRes(call.getId(), conv.getId(), caller.getId(), receiver.getId(), call.getStatus().name(), call.getType().name(), call.getCreatedAt());
    }

    @Transactional
    public void accept(Long callId, Long userId) {
        Call call = callRepo.findById(callId).orElseThrow();
        CallParticipant me = cpRepo.findOne(callId, userId).orElseThrow();

        me.setState(ParticipantState.ACCEPTED);
        me.setJoinedAt(Instant.now());
        cpRepo.save(me);

        call.setStatus(CallStatus.ONGOING);
        call.setStartedAt(Instant.now());
        callRepo.save(call);

        publisher.publishEvent(new CallAcceptedEvent(callId, userId));
    }

    @Transactional
    public void reject(Long callId, Long userId) {
        Call call = callRepo.findById(callId).orElseThrow();
        CallParticipant me = cpRepo.findOne(callId, userId).orElseThrow();

        me.setState(ParticipantState.REJECTED);
        cpRepo.save(me);

        call.setStatus(CallStatus.REJECTED);
        call.setEndedAt(Instant.now());
        callRepo.save(call);

        publisher.publishEvent(new CallRejectedEvent(callId, userId));
    }

    @Transactional
    public ResponseEntity<ResponseObject> end(Long callId) throws APIException {

//        publisher.publishEvent(new CallEndedEvent(callId, call.getCaller().getId(), duration));




        Call call = callRepo.findById(callId).orElseThrow(()-> new APIException(ErrorCode.CALL_NOT_FOUND));

        call.setStatus(CallStatus.ENDED);



        Instant endedAt = Instant.now();
        call.setEndedAt(endedAt);

        Long duration = Duration.between(call.getStartedAt(), endedAt).getSeconds();

        callRepo.save(call);

        User sender = call.getCaller();

        Conversation conv = call.getConversation();



        Long maxSeq = messageRepo.maxSeq(conv.getId());
        Long nextSeq = (maxSeq == null ? 1L : maxSeq + 1);

        ConversationMember senderMember = conversationMemberRepository.findByIdConversationIdAndIdUserId(conv.getId(),sender.getId()).orElseThrow(
                ()-> new APIException(ErrorCode.SENDER_NOT_IN_CONVERSATION));

        Message msg = new Message();
        msg.setConversation(conv);
        msg.setSender(sender);
        msg.setSenderMember(senderMember);
        msg.setType(MessageType.CALL_VOICE);
        msg.setContent(duration+"");
        msg.setSeq(nextSeq);
        msg.setReplyTo(null);

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
}
