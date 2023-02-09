package com.project.sns.service;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private PostRepository postRepository;

    @DisplayName("[Service][Post] Given Parameters - When Creating Post - Success")
    @Test
    void givenParameters_whenCreatingPost_thenSuccess() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        String username = "username";
        Optional<UserEntity> optionalUserEntity = createOptionalUserEntity(username);
        given(userEntityRepository.findByUsername(username)).willReturn(optionalUserEntity);
        given(postRepository.save(any())).willReturn(mock(PostEntity.class));

        // When & Then
        Assertions.assertDoesNotThrow(() -> postService.create(title, body, username));
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().save(any());
    }

    @DisplayName("[Service][Post] Given Non Existing User - When Create Post - Fails")
    @Test
    void givenNonExistingUser_whenCreatingPost_thenFails() throws Exception {
        // Given
        String title = "title";
        String body = "body";
        String username = "username_non_existent";
        given(userEntityRepository.findByUsername(username)).willReturn(Optional.empty());

        // When & Then
        Assertions.assertThrows(SnsApplicationException.class, () -> postService.create(title, body, username));
        then(userEntityRepository).should().findByUsername(username);

    }

    @DisplayName("[Service][Post] Given Valid Parameters - When Updating Post - Success")
    @Test
    void givenParameters_whenUpdatingPost_thenSuccess() throws Exception {
        // Given
        Long postId = 1L;
        String title = "title";
        String body = "body";
        String username = "username";
        Optional<UserEntity> userEntity = createOptionalUserEntity(username);
        PostEntity postEntity = createPostEntity(postId, title, body, userEntity.get());

        given(userEntityRepository.findByUsername(any())).willReturn(userEntity);
        given(postRepository.getReferenceById(any())).willReturn(postEntity);

        // When & Then
        Assertions.assertDoesNotThrow(() -> postService.update(title, body, username, postId));
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().getReferenceById(postId);
    }

    @DisplayName("[Service][Post] Given Non Existent Post - When Updating Post - Fails")
    @Test
    void givenNonExistentPost_whenUpdatingPost_thenFails() throws Exception {
        // Given
        Long invalidPostId = 2L;
        String title = "title";
        String body = "body";
        String username = "username";
        Optional<UserEntity> userEntity = createOptionalUserEntity(username);

        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);
        given(postRepository.getReferenceById(invalidPostId)).willReturn(null);

        // When & Then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.update(title, body, username, invalidPostId));
        Assertions.assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().getReferenceById(invalidPostId);
    }

    @DisplayName("[Service][Post] Given Non Matching Username - When Updating Post - Fails")
    @Test
    void givenNonMatchingUsername_whenUpdatingPost_thenFails() throws Exception {
        // Given
        Long postId = 1L;
        String title = "title";
        String body = "body";
        String username = "username";
        String postUsername = "postUsername";

        Optional<UserEntity> userEntity = createOptionalUserEntity(username);
        Optional<UserEntity> postUserEntity = createOptionalUserEntity(postUsername);
        PostEntity postEntity = createPostEntity(postId, title, body, postUserEntity.get());

        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);
        given(postRepository.getReferenceById(postId)).willReturn(postEntity);

        // When & Then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.update(title, body, username, postId));
        Assertions.assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().getReferenceById(postId);
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

    private static PostEntity createPostEntity(Long postId, String title, String body, UserEntity userEntity) {
        return PostEntity.of(
                postId,
                title,
                body,
                userEntity,
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())
        );
    }
}