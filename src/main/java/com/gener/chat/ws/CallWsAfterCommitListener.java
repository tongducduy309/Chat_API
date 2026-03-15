package com.gener.chat.ws;

import com.gener.chat.handlers.WebSocketHandler;
import com.gener.chat.repositories.CallParticipantRepository;
import com.gener.chat.ws.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallWsAfterCommitListener {
    private final WebSocketHandler ws;
    private final ObjectMapper om;
    private final CallParticipantRepository cpRepo;

    private void push(Long userId, String event, Object data) {
        try {
            ws.push(userId, Map.of("event", event, "data", data));
        } catch (Exception ignored) {}
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIncoming(IncomingCallCallerEvent e) {
        push(e.receiverId(), "INCOMING_CALL", e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIncoming(IncomingCallReceiverEvent e) {
        push(e.callerId(), "CALL_RINGING", e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAccepted(CallAcceptedEvent e) {
        for (Long uid : cpRepo.findUserIds(e.callId())) push(uid, "CALL_ACCEPTED", e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRejected(CallRejectedEvent e) {
        for (Long uid : cpRepo.findUserIds(e.callId())) push(uid, "CALL_REJECTED", e);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onEnded(CallEndedEvent e) {
        for (Long uid : cpRepo.findUserIds(e.callId())) push(uid, "CALL_ENDED", e);
    }
}
