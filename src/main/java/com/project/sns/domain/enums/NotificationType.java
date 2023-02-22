package com.project.sns.domain.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    NEW_LIKE_ON_POST("New Like."), NEW_COMMENT_ON_POST("New Comment.");

    private String notificationMessage;

    NotificationType(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }
}
