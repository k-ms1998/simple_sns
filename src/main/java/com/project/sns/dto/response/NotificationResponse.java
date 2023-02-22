package com.project.sns.dto.response;

import com.project.sns.domain.NotificationArgs;
import com.project.sns.domain.enums.NotificationType;
import com.project.sns.dto.entity.NotificationDto;
import com.project.sns.dto.entity.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private NotificationType notificationType;
    private NotificationArgs notificationArgs;
    private String message;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static NotificationResponse fromDto(NotificationDto dto) {
        return new NotificationResponse(
                dto.getId(),
                dto.getNotificationType(),
                dto.getNotificationArgs(),
                dto.getNotificationType().getNotificationMessage(),
                dto.getRegisteredAt(),
                dto.getUpdatedAt(),
                dto.getDeletedAt()
        );
    }
}
