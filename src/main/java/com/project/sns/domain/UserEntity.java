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
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATED \"user\" SET deleted_at = NOW() where id=?") // sql delete 가 일어날떄 deleted_at 값 업데이트
@Where(clause = "deleted_at is NULL") // select 할때 해당 where 문도 추가되어서 실행 -> deleted_at 이 NULL 이면 삭제되지 않은 튜플들만 반환
@Table(name = "\"user\"") // PostgreSQL 에서는 이미 user라는 테이블이 존재하기 때문에, 우리가 설계한 user 테이블이라는 것을 알려주기 위해서 "user" 으로 표신
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

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

    public static UserEntity of(String username, String password) {
        return UserEntity.of(null, username, password, null, null, null, null);
    }

    public static UserEntity of(String username, String password, UserRole userRole) {
        return UserEntity.of(null, username, password, userRole, null, null, null);
    }

    public static UserEntity of(Long id, String username, String password, UserRole userRole, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new UserEntity(id, username, password, userRole, registeredAt, updatedAt, deletedAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getPassword(), that.getPassword()) && getUserRole() == that.getUserRole();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUsername(), getPassword(), getUserRole());
    }
}
