package com.project.sns.domain;

import com.project.sns.domain.enums.NotificationType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE notification SET deleted_at = NOW() where id=?") // sql delete 가 일어날떄 deleted_at 값 업데이트
@Where(clause = "deleted_at is NULL") // select 할때 해당 where 문도 추가되어서 실행 -> deleted_at 이 NULL 이면 삭제되지 않은 튜플들만 반환
@Table(name = "notification",
        indexes = {
                @Index(name = "user_id_idx", columnList = "user_id")
        }
)
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알람을 받는 사람
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType notificationType;

    /**
     * NotificationArgs: 알림의 정보 저장 -> 알림을 발생 시킨 유저, 알림 타입, 알림을 일으킨 포스트/댓글 등등
     *  => 추후에 파라미터들이 추가될 여지가 많음
     *      => 그러므로, json 타입으로 DB에 저장
     *          => MariaDB 에서 json 으로 컬럼을 매핑하기 위해서는 'longtext' 로 정의
     */
    @Type(JsonType.class)
    @Column(columnDefinition = "longtext")
    private NotificationArgs notificationArgs;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    public static NotificationEntity of(UserEntity userEntity, NotificationType notificationType, NotificationArgs notificationArgs) {
        return NotificationEntity.of(null, userEntity, notificationType, notificationArgs, null, null, null);
    }

    public static NotificationEntity of(Long id, UserEntity userEntity, NotificationType notificationType, NotificationArgs notificationArgs, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new NotificationEntity(
                id,
                userEntity,
                notificationType,
                notificationArgs,
                registeredAt,
                updatedAt,
                deletedAt
        );
    }
}
