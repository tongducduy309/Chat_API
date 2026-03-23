package com.gener.chat.services;

import com.gener.chat.models.User;
import com.gener.chat.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private static final Duration ONLINE_TTL = Duration.ofSeconds(60);
    private static final Duration SOCKET_SET_TTL = Duration.ofHours(12);

    private final StringRedisTemplate stringRedisTemplate;
    private final UserRepository userRepository;

    private String onlineKey(Long userId) {
        return "presence:user:" + userId;
    }

    private String socketKey(Long userId) {
        return "presence:sockets:" + userId;
    }

    public void connect(Long userId, String sessionId) {
        stringRedisTemplate.opsForSet().add(socketKey(userId), sessionId);
        stringRedisTemplate.expire(socketKey(userId), SOCKET_SET_TTL);

        stringRedisTemplate.opsForValue().set(onlineKey(userId), "online", ONLINE_TTL);
    }

    public void heartbeat(Long userId) {
        stringRedisTemplate.opsForValue().set(onlineKey(userId), "online", ONLINE_TTL);
        stringRedisTemplate.expire(socketKey(userId), SOCKET_SET_TTL);
    }

    public void disconnect(Long userId, String sessionId) {
        stringRedisTemplate.opsForSet().remove(socketKey(userId), sessionId);

        Long remain = stringRedisTemplate.opsForSet().size(socketKey(userId));
        if (remain == null || remain == 0) {
            stringRedisTemplate.delete(onlineKey(userId));
            updateLastSeen(userId);
        }
    }

    public boolean isOnline(Long userId) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(onlineKey(userId)));
    }

    public long onlineCount() {
        Long count = stringRedisTemplate.keys("presence:user:*").stream().count();
        return count;
    }

    public void forceOffline(Long userId) {
        stringRedisTemplate.delete(onlineKey(userId));
        stringRedisTemplate.delete(socketKey(userId));
        updateLastSeen(userId);
    }

    private void updateLastSeen(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastSeenAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }
}