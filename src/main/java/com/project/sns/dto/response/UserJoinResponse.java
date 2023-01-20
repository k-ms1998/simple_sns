package com.project.sns.dto.response;

import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinResponse {

    private Long id;
    private String username;
    private UserRole userRole;

    public static UserJoinResponse of(Long id, String username, UserRole userRole) {
        return new UserJoinResponse(id, username, userRole);
    }

    public static UserJoinResponse from(UserEntity userEntity) {
        return UserJoinResponse.of(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getUserRole()
        );
    }


}
