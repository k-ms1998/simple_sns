package com.project.sns.service;

import com.project.sns.domain.NotificationArgs;
import com.project.sns.domain.NotificationEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.NotificationType;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.NotificationEntityRepository;
import com.project.sns.repository.SseEmitterRepository;
import com.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 100000L * 60L;
    private static final String NOTIFICATION_NAME = "alarm"; // front-end 에서 알람을 가져오는 eventHandler 의 이름이랑 같도록 설정해서, SseEmitter 랑 연결

    private final SseEmitterRepository sseEmitterRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public SseEmitter connectNotification(Long userId) {
        SseEmitter sseEmitter = sseEmitterRepository.save(userId, new SseEmitter(DEFAULT_TIMEOUT));
        log.info("connected: {}", userId);

        sseEmitter.onCompletion(() -> sseEmitterRepository.delete(userId));
        sseEmitter.onTimeout(() -> sseEmitterRepository.delete(userId));

        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id("id")
                            .name(NOTIFICATION_NAME)
                            .data("Connection Completed.")
            );

        } catch (IOException e) {
            throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECTION_ERROR, String.format("{}", userId));
        }

        return sseEmitter;
    }

    public void send(NotificationType type, NotificationArgs args, UserEntity receiver) {
        NotificationEntity notificationEntity = notificationEntityRepository.save(NotificationEntity.of(receiver, type, args));

        sseEmitterRepository.get(receiver.getId())
                .ifPresentOrElse(sseEmitter -> {
                    log.info("sseEmitter: {}", sseEmitter.toString());
                    try {
                        sseEmitter.send(
                                SseEmitter.event()
                                        .id(notificationEntity.getId().toString())
                                        .name(NOTIFICATION_NAME)
                                        .data("sent")
                        );
                    } catch (IOException e) {
//                        sseEmitter.complete();
//                        log.error("e : {}", e.getMessage());
                        sseEmitterRepository.delete(receiver.getId());
                        throw new SnsApplicationException(ErrorCode.NOTIFICATION_CONNECTION_ERROR);
                    }
                }, () -> log.info("SseEmitter not found."));

    }

    public void send(NotificationType type, NotificationArgs args, Long receiverId){
        UserEntity userEntity = userEntityRepository.findById(receiverId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER));

        this.send(type, args, userEntity);
    }

}
