package com.project.sns.kafka.producer;

import com.project.sns.dto.event.NotificationEvent;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    public void send(NotificationEvent event){
        kafkaTemplate.send(topic, event.getReceiverId(), event);
        log.info("Sent to Kafka.");
    }

}
