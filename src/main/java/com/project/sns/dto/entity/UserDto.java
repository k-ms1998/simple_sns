package com.project.sns.dto.entity;

import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String password;
    private UserRole userRole;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static UserDto of(Long id, String username, String password, UserRole userRole, Timestamp registeredAt, Timestamp updatedAt, Timestamp deletedAt) {
        return new UserDto(id, username, password, userRole, registeredAt, updatedAt, deletedAt);
    }

    public static UserDto from(UserEntity userEntity) {
        return UserDto.of(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getPassword(),
                userEntity.getUserRole(),
                userEntity.getRegisteredAt(),
                userEntity.getUpdatedAt(),
                userEntity.getDeletedAt()
        );
    }

}
