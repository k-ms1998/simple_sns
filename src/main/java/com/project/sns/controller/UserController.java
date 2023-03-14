package com.project.sns.controller;

import com.project.sns.domain.UserEntity;
import com.project.sns.dto.entity.UserDto;
import com.project.sns.dto.request.UserJoinRequest;
import com.project.sns.dto.request.UserLoginRequest;
import com.project.sns.dto.response.NotificationResponse;
import com.project.sns.dto.response.ResponseBody;
import com.project.sns.dto.response.UserJoinResponse;
import com.project.sns.dto.response.UserLoginResponse;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.service.NotificationService;
import com.project.sns.service.UserEntityService;
import com.project.sns.util.ClassUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserEntityService userEntityService;
    private final NotificationService notificationService;

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
        UserDto userDto = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);

        Page<NotificationResponse> notifications = userEntityService.fetchNotifications(pageable, userDto.getId())
                .map(NotificationResponse::fromDto);

        return ResponseBody.success("Success", notifications);
    }

    // '/users/notifications/subscribe?token={token}' => 토큰이 헤더가 아닌 Param 으로 주어짐 => JwtTokenFilter 에서 해당 api 가 호출되면, 헤더가 아니라 param 을 통해 토큰을 가져오도록 설정
    @GetMapping("/notifications/subscribe")
    public SseEmitter subscribe(Authentication authentication) {
        UserDto userDto = ClassUtils.getSafeCastInstance(authentication.getPrincipal(), UserDto.class);

        return notificationService.connectNotification(userDto.getId());
    }

}
