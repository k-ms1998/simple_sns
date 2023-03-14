package com.project.sns.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Instance 자체를 저장
 * */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SseEmitterRepository {

    private Map<String, SseEmitter> sseEmitterMap = new HashMap<>();

    // 알림을 요청 받을 유저의 웹 브라우저로 알림 전송 -> 유저를 판별할 userId 필요
    public SseEmitter save(Long userId, SseEmitter sseEmitter) {
        final String key = getKey(userId);

        log.info("Set SseEmitter: {},{}", userId, key);
        sseEmitterMap.put(key, sseEmitter);

        return sseEmitter;
    }

    public void delete(Long userId) {
        final String key = getKey(userId);
        log.info("Delete SseEmitter: {}", userId);
        sseEmitterMap.remove(key);
    }

    public Optional<SseEmitter> get(Long userId) {
        final String key = getKey(userId);
        SseEmitter result = sseEmitterMap.get(key);
        log.info("Get SseEmitter: {}", result);

        return Optional.ofNullable(result);
    }

    private String getKey(Long userId) {
        return "SseEmitter:UID:" + userId;
    }
}
