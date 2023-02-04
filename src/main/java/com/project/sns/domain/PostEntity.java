package com.project.sns.domain;

import com.project.sns.domain.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATED \"post\" SET deleted_at = NOW() where id=?") // sql delete 가 일어날떄 deleted_at 값 업데이트
@Where(clause = "deleted_at is NULL") // select 할때 해당 where 문도 추가되어서 실행 -> deleted_at 이 NULL 이면 삭제되지 않은 튜플들만 반환
@Table(name = "\"post\"")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    @Column(nullable = false)
    private String body;

    @ManyToOne
    @JoinColumn(name = "userId")
    private UserEntity userEntity;

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

    public static PostEntity of(String title, String body, UserEntity userEntity) {
        return PostEntity.of(null, title, body, userEntity, null, null, null);
    }

    public static PostEntity of(Long id, String title, String body, UserEntity userEntity, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new PostEntity(id, title, body, userEntity, registeredAt, updatedAt, deletedAt);
    }
}
