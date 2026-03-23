package com.gener.chat.handlers;

import com.gener.chat.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper om;
    private final PresenceService presenceService;

    // multi-device: userId -> sessions
    private final Map<Long, Set<WebSocketSession>> sessions = new ConcurrentHashMap<>();

    private Long extractUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null) return null;

        for (String p : uri.getQuery().split("&")) {
            String[] kv = p.split("=");
            if (kv.length == 2 && kv[0].equals("userId")) {
                try {
                    return Long.parseLong(kv[1]);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = extractUserId(session);
        if (userId == null) {
            try {
                session.close();
            } catch (Exception ignored) {
            }
            return;
        }

        sessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);

        // Redis online
        presenceService.connect(userId, session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long disconnectedUserId = null;

        for (Map.Entry<Long, Set<WebSocketSession>> entry : sessions.entrySet()) {
            Long userId = entry.getKey();
            Set<WebSocketSession> set = entry.getValue();

            boolean removed = set.removeIf(s -> s.getId().equals(session.getId()));
            if (removed) {
                disconnectedUserId = userId;

                if (set.isEmpty()) {
                    sessions.remove(userId);
                }
                break;
            }
        }

        if (disconnectedUserId != null) {
            // Redis offline nếu không còn session nào
            presenceService.disconnect(disconnectedUserId, session.getId());
        }
    }

    // server -> client
    public void push(Long userId, Object payload) {
        Set<WebSocketSession> set = sessions.get(userId);
        if (set == null || set.isEmpty()) return;

        for (WebSocketSession s : set) {
            if (!s.isOpen()) continue;
            try {
                s.sendMessage(new TextMessage(om.writeValueAsString(payload)));
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            JsonNode root = om.readTree(message.getPayload());
            String event = root.path("event").asText(null);
            long toUserId = root.path("toUserId").asLong(-1);
            Long fromUserId = extractUserId(session);

            if (event == null || toUserId <= 0 || fromUserId == null) return;

            // heartbeat presence từ client
            if ("PING".equals(event)) {
                presenceService.heartbeat(fromUserId);
                return;
            }

            JsonNode data = root.path("data");
            var outbound = Map.of(
                    "event", event,
                    "data", Map.of(
                            "fromUserId", fromUserId,
                            "toUserId", toUserId,
                            "data", data
                    )
            );

            push(toUserId, outbound);
        } catch (Exception ignored) {
        }
    }
}