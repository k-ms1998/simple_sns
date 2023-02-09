package com.project.sns.dto.entity;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String body;
    private UserDto userDto;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static PostDto of(Long id, String title, String body, UserEntity userEntity, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new PostDto(
                id,
                title,
                body,
                userEntity.toDto(),
                registeredAt,
                updatedAt,
                deletedAt
        );
    }

    public static PostDto from(PostEntity entity) {
        return new PostDto(
                entity.getId(),
                entity.getTitle(),
                entity.getBody(),
                entity.getUserEntity().toDto(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
