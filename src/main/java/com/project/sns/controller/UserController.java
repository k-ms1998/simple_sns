package com.project.sns.controller;

import com.project.sns.domain.UserEntity;
import com.project.sns.dto.request.UserJoinRequest;
import com.project.sns.dto.request.UserLoginRequest;
import com.project.sns.dto.response.NotificationResponse;
import com.project.sns.dto.response.ResponseBody;
import com.project.sns.dto.response.UserJoinResponse;
import com.project.sns.dto.response.UserLoginResponse;
import com.project.sns.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseBody<UserLoginResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
        String token = userEntityService.login(userLoginRequest.getUsername(), userLoginRequest.getPassword());

        return ResponseBody.success("OK", UserLoginResponse.of(token));
    }

    @GetMapping("/notifications")
    public ResponseBody<Page<NotificationResponse>> fetchNotifications(Pageable pageable, Authentication authentication) {
        Page<NotificationResponse> notifications = userEntityService.fetchNotifications(pageable, authentication.getName())
                .map(NotificationResponse::fromDto);

        return ResponseBody.success("Success", notifications);
    }

}
