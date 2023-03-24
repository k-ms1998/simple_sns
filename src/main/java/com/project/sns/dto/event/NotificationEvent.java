package com.project.sns.dto.event;

import com.project.sns.domain.NotificationArgs;
import com.project.sns.domain.NotificationEntity;
import com.project.sns.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private Long receiverId;
    private NotificationType notificationType;
    private NotificationArgs notificationArgs;

    public static NotificationEvent of(Long receiverId, NotificationType notificationType, NotificationArgs notificationArgs) {
        return new NotificationEvent(receiverId, notificationType, notificationArgs);
    }

    public static NotificationEvent fromEntity(NotificationEntity entity) {
        return NotificationEvent.of(
                entity.getUserEntity().getId(),
                entity.getNotificationType(),
                entity.getNotificationArgs()
        );
    }

}
