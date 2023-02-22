package com.project.sns.domain;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationArgs {

    // 알람을 발생 시킨 사람 -> ex: 내 포스트에 좋아요를 누른 사람의 user_id
    private Long fromUserId;

    // 알림이 발생한 객체의 id -> ex: 내 포스트에 좋요요를 누르면, 해당 포스트의 post_id
    private Long targetId;

    public static NotificationArgs of(Long fromUserId, Long targetId) {
        return new NotificationArgs(fromUserId, targetId);
    }
    
}
