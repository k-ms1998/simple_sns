package com.project.sns.kafka.consumer;

import com.project.sns.dto.event.NotificationEvent;
import com.project.sns.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${spring.kafka.topic.notification}")
    public void consume(NotificationEvent event, Acknowledgment ack) {
        log.info("Consume event: {}", event);
        notificationService.send(event.getNotificationType(), event.getNotificationArgs(), event.getReceiverId());

        ack.acknowledge();
    }
}
