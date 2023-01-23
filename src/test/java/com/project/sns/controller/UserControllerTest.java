package com.project.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.config.SpringSecurityConfig;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.dto.request.UserJoinRequest;
import com.project.sns.dto.request.UserLoginRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.service.UserEntityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SpringSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserEntityService userEntityService;

    @DisplayName("[View] Given Non-Existing Username - Saves User - Success")
    @Test
    void givenNonExistingUsername_whenJoining_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserJoinRequest userJoinRequest = createUserJoinRequest(username, password);
        given(userEntityService.join(username, password)).willReturn(UserEntity.of(username, password, UserRole.USER));

        // When && Then
        mockMvc.perform(post("/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        then(userEntityService).should().join(username, password);
    }

    @DisplayName("[View] Given Existing Username - Fails")
    @Test
    void givenExistingUsername_whenJoining_thenFails() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserJoinRequest userJoinRequest = createUserJoinRequest(username, password);
        UserEntity userEntity = createUser(username, password);
        given(userEntityService.join(username, password)).willThrow(new SnsApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("%s already exists.", username)));

        // When && Then
        mockMvc.perform(post("/users/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());

        then(userEntityService).should().join(username, password);
    }

    @DisplayName("[View] Given Valid Username - Login User - Success")
    @Test
    void givenValidUsername_whenLoggingIn_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserLoginRequest userLoginRequest = createUserLoginRequest(username, password);
        given(userEntityService.login(username, password)).willReturn("test_token");

        // When && Then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        then(userEntityService).should().login(username, password);
    }

    @DisplayName("[View] Given Invalid Username - Login User - Fails")
    @Test
    void givenInValidUsername_whenLoggingIn_thenFails() throws Exception {
        // Given
        String username = "wronguser";
        String password = "password";
        UserLoginRequest userLoginRequest = createUserLoginRequest(username, password);
        given(userEntityService.login(username, password))
                .willThrow(new SnsApplicationException(ErrorCode.NON_EXISTING_USER, String.format("User \'%s\' doesn't exist.", username)));

        // When && Then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        then(userEntityService).should().login(username, password);
    }

    @DisplayName("[View] Given Valid Password - Login User - Success")
    @Test
    void givenValidPassword_whenLoggingIn_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserLoginRequest userLoginRequest = createUserLoginRequest(username, password);
        given(userEntityService.login(username, password)).willReturn("test_token");

        // When && Then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());

        then(userEntityService).should().login(username, password);
    }

    @DisplayName("[View] Given Invalid Password - Login User - Fails")
    @Test
    void givenInValidPassword_whenLoggingIn_thenFails() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserLoginRequest userLoginRequest = createUserLoginRequest(username, password);
        given(userEntityService.login(username, password))
                .willThrow(new SnsApplicationException(ErrorCode.INCORRECT_PASSWORD, ""));

        // When && Then
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        then(userEntityService).should().login(username, password);
    }

    private static UserJoinRequest createUserJoinRequest(String username, String password) {
        return UserJoinRequest.of(username, password);
    }

    private static UserEntity createUser(String username, String password) {
        return UserEntity.of(username, password);
    }

    private static UserLoginRequest createUserLoginRequest(String username, String password) {
        return UserLoginRequest.of(username, password);
    }
}