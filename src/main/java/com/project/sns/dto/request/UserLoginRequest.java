package com.project.sns.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {

    private String username;
    private String password;

    public static UserLoginRequest of(String username, String password) {
        return new UserLoginRequest(username, password);
    }
}
