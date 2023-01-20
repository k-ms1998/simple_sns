package com.project.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserJoinRequest {

    private String username;
    private String password;

    public static UserJoinRequest of(String username, String password) {
        return new UserJoinRequest(username, password);
    }
}
