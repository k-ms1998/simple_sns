package com.project.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.dto.request.PostCreateRequest;
import com.project.sns.dto.request.PostUpdateRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserEntityRepository;
import com.project.sns.service.PostService;
import com.project.sns.util.JwtTokenUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private PostService postService;

    @MockBean
    private PostRepository postRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @DisplayName("[Controller][Post] Creating Post - Success")
    @Test
    @WithMockUser
    void givenParameters_whenSavingPost_thenSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        String username = "username";
        PostCreateRequest request = createPostCreateRequest(title, body);
        String testToken = JwtTokenUtils.generateToken(username, secretKey, 10000000L);
        Optional<UserEntity> userEntity = createOptionalUserEntity(username);

        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);
        doNothing().when(postService).create(any(), any(), any());

    // When & Then
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
        then(userEntityRepository).should().findByUsername(username);
    }

    @DisplayName("[Controller][Post] Creating Post - Not Logged In - Fail")
    @Test
    @WithAnonymousUser
    void givenParameters_whenSavingPostAndNotLoggedIn_thenFail() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        PostCreateRequest request = createPostCreateRequest(title, body);

        // When & Then
        mockMvc.perform(post("/post/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                ).andDo(print())
                .andExpect(status().isUnauthorized());


    }

    @DisplayName("[Controller][Post] Updating Post - Success")
    @Test
    @WithMockUser
    void givenParameters_whenUpdatingPost_thenSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        String username = "username";
        PostUpdateRequest request = createPostUpdateRequest(title, body);
        String testToken = JwtTokenUtils.generateToken(username, secretKey, 10000000L);
        Optional<UserEntity> userEntity = createOptionalUserEntity(username);

        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);
        doNothing().when(postService).update(any(), any(), any(), any());

        // When & Then
        mockMvc.perform(post("/post/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
                )
                .andDo(print())
                .andExpect(status().isOk());
        then(userEntityRepository).should().findByUsername(username);
    }

    @DisplayName("[Controller][Post] Given Incorrect Username - Updating Post - Throws Error")
    @Test
    @WithMockUser
    void givenWrongUsername_whenUpdatingPost_thenSuccess() throws Exception {
        // Given
        Long postId = 1L;
        String title = "title";
        String body = "body";
        String username = "username";

        PostUpdateRequest request = createPostUpdateRequest(title, body);
        String testToken = JwtTokenUtils.generateToken(username, secretKey, 10000000L);
        Optional<UserEntity> userEntity = createOptionalUserEntity(username);

        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);

        // When & Then
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND, "")).when(postService).update(any(), any(), eq(username), eq(postId));
        mockMvc.perform(post("/post/update/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + testToken)
                )
                .andDo(print())
                .andExpect(status().isNotFound());
        then(userEntityRepository).should().findByUsername(username);
    }

    private static PostCreateRequest createPostCreateRequest(String title, String body) {
        return PostCreateRequest.of(title, body);
    }

    private static PostUpdateRequest createPostUpdateRequest(String title, String body) {
        return PostUpdateRequest.of(title, body);
    }

    private static Optional<UserEntity> createOptionalUserEntity(String username) {
        return Optional.of(UserEntity.of(
                        1L,
                        username,
                        "password",
                        UserRole.USER,
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now())
                )
        );
    }

    private static PostEntity createPostEntity(UserEntity userEntity) {
        return PostEntity.of(
                1L,
                "title",
                "body",
                userEntity,
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())
        );
    }
}