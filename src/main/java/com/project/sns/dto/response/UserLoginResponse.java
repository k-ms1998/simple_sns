package com.project.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {

    private String token;

    public static UserLoginResponse of(String token) {
        return new UserLoginResponse(token);
    }
}
