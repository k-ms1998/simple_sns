package com.project.sns.controller;

import com.project.sns.domain.UserEntity;
import com.project.sns.dto.request.UserJoinRequest;
import com.project.sns.dto.request.UserLoginRequest;
import com.project.sns.dto.response.ResponseBody;
import com.project.sns.dto.response.UserJoinResponse;
import com.project.sns.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserEntityService userEntityService;

    @PostMapping("/join")
    public ResponseBody<UserJoinResponse> joinUser(@RequestBody UserJoinRequest userJoinRequest) {
        UserEntity userEntity = userEntityService.join(userJoinRequest.getUsername(), userJoinRequest.getPassword());

        return ResponseBody.success("OK", UserJoinResponse.from(userEntity));
    }

    @PostMapping("/login")
    public void loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        userEntityService.login(userLoginRequest.getUsername(), userLoginRequest.getPassword());
    }
}
