package com.project.sns.dto.entity;

import com.project.sns.domain.NotificationArgs;
import com.project.sns.domain.NotificationEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.NotificationType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private UserDto user;
    private NotificationType notificationType;
    private NotificationArgs notificationArgs;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static NotificationDto fromEntity(NotificationEntity entity) {
        return new NotificationDto(
                entity.getId(),
                entity.getUserEntity().toDto(),
                entity.getNotificationType(),
                entity.getNotificationArgs(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
