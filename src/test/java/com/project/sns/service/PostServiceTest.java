package com.project.sns.service;

import com.project.sns.domain.*;
import com.project.sns.domain.enums.NotificationType;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.dto.request.CommentCreateRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @MockBean
    private UpVoteEntityRepository upVoteEntityRepository;

    @MockBean
    private CommentEntityRepository commentEntityRepository;

    @MockBean
    private NotificationEntityRepository notificationEntityRepository;

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
        given(postRepository.findById(any())).willReturn(Optional.of(postEntity));
        given(postRepository.save(any())).willReturn(postEntity);

        // When & Then
        Assertions.assertDoesNotThrow(() -> postService.update(title, body, username, postId));
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().findById(any());
        then(postRepository).should().save(any());
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
        given(postRepository.findById(invalidPostId)).willReturn(Optional.empty());

        // When & Then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.update(title, body, username, invalidPostId));
        Assertions.assertEquals(e.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().findById(invalidPostId);
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
        given(postRepository.findById(any())).willReturn(Optional.of(postEntity));

        // When & Then
        SnsApplicationException e = Assertions.assertThrows(SnsApplicationException.class, () -> postService.update(title, body, username, postId));
        Assertions.assertEquals(e.getErrorCode(), ErrorCode.INVALID_PERMISSION);
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().findById(any());
    }

    @DisplayName("[Service][Post] Given Parameters - When Deleting Post - Success")
    @Test
    void givenParameters_whenDeletingPost_thenSuccess() throws Exception {
        // Given
        String username = "username";
        Long postId = 1L;
        Optional<UserEntity> optionalUserEntity = createOptionalUserEntity(username);
        PostEntity postEntity = createPostEntity(postId, "title", "body", optionalUserEntity.get());

        given(userEntityRepository.findByUsername(username)).willReturn(optionalUserEntity);
        given(postRepository.findById(any())).willReturn(Optional.of(postEntity));

        // When & Then
        Assertions.assertDoesNotThrow(() -> postService.delete(username, postId));
        then(userEntityRepository).should().findByUsername(username);
        then(postRepository).should().findById(any());
    }

    @DisplayName("[Service][Post] Given Parameters - When Fetching All Posts - Success")
    @Test
    void givenParameters_whenFetchingAllPosts_thenSuccess() throws Exception {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        given(postRepository.findAll(pageable)).willReturn(Page.empty());

        // When && Then
        Assertions.assertDoesNotThrow(() -> postService.fetchAllPosts(pageable));
        then(postRepository).should().findAll(pageable);
    }

    @DisplayName("[Service][Post] Given Parameters - When Fetching My Posts - Success")
    @Test
    void givenParameters_whenFetchingMyPosts_thenSuccess() throws Exception {
        // Given
        Pageable pageable = Pageable.ofSize(10);
        String username = "username";
        UserEntity userEntity = createOptionalUserEntity(username).get();
        given(userEntityRepository.findByUsername(username)).willReturn(Optional.of(userEntity));
        given(postRepository.findAllByUserEntity(pageable, userEntity)).willReturn(Page.empty());

        // When && Then
        Assertions.assertDoesNotThrow(() -> postService.fetchMyPosts(pageable, username));
        then(postRepository).should().findAllByUserEntity(pageable, userEntity);
        then(userEntityRepository).should().findByUsername(username);
    }

    @DisplayName("[Service][Get] Given Parameters - When Fetching Up Vote Counts - Success")
    @Test
    void givenParameters_whenFetchingUpVoteCounts_thenSuccess() throws Exception {
        // Given
        Long postId = 1L;
        PostEntity postEntity = createPostEntity(postId, "title", "body", UserEntity.of("username", "password"));

        given(postRepository.findById(postId)).willReturn(Optional.of(postEntity));
        given(upVoteEntityRepository.findByPostEntityAndUpVoted(postEntity, true)).willReturn(1L);

        // When
        Long result = postService.fetchUpVotesCount(postId);

        // Then
        Assertions.assertEquals(1L, result);
        then(upVoteEntityRepository).should().findByPostEntityAndUpVoted(postEntity, true);
        then(postRepository).should().findById(postId);
    }

    @DisplayName("[Service] Given Comment Create Request - When Adding Comment - Success")
    @Test
    void givenCommentCreateRequest_whenAddingComment_thenSuccess() throws Exception {
        // Given
        Long postId = 1L;
        String usernameA = "usernameA";
        String usernameB = "usernameB";
        CommentCreateRequest request = createCommentCreateRequest("comment");
        UserEntity userEntityA = createUserEntity(1L, usernameA);
        UserEntity userEntityB = createUserEntity(2L, usernameB);
        PostEntity postEntity = createPostEntity(postId, "title", "body", userEntityA);

        given(userEntityRepository.findByUsername(usernameB)).willReturn(Optional.of(userEntityB));
        given(postRepository.findById(any())).willReturn(Optional.of(postEntity));
        given(commentEntityRepository.save(any())).willReturn(CommentEntity.of("comment", userEntityB, postEntity));
        given(notificationEntityRepository.save(any()))
                .willReturn(NotificationEntity.of(
                        userEntityB, NotificationType.NEW_COMMENT_ON_POST, NotificationArgs.of(postEntity.getUserEntity().getId(), postId)
                ));

        // When && Then
        Assertions.assertDoesNotThrow(() -> postService.addComment(request, postId, usernameB));

        then(userEntityRepository).should().findByUsername(any());
        then(postRepository).should().findById(any());
        then(commentEntityRepository).should().save(any());
        then(notificationEntityRepository).should().save(any());
    }

    @DisplayName("[Service] Given User Comments On Own Post - When Adding Comment - Success And Not Create Notification")
    @Test
    void givenUserCommentsOnOwnPost_whenAddingComment_thenSuccessAndNotCreateNotification() throws Exception {
        // Given
        Long postId = 1L;
        String username = "username";
        CommentCreateRequest request = createCommentCreateRequest("comment");
        UserEntity userEntity = createUserEntity(1L, username);
        PostEntity postEntity = createPostEntity(postId, "title", "body", userEntity);

        given(userEntityRepository.findByUsername(username)).willReturn(Optional.of(userEntity));
        given(postRepository.findById(any())).willReturn(Optional.of(postEntity));
        given(commentEntityRepository.save(any())).willReturn(CommentEntity.of("comment", userEntity, postEntity));

        // When && Then
        Assertions.assertDoesNotThrow(() -> postService.addComment(request, postId, username));

        then(userEntityRepository).should().findByUsername(any());
        then(postRepository).should().findById(any());
        then(commentEntityRepository).should().save(any());
        then(notificationEntityRepository).shouldHaveNoInteractions();
    }

    private static CommentCreateRequest createCommentCreateRequest(String comment) {
        return CommentCreateRequest.of(comment);
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

    private static UserEntity createUserEntity(Long id, String username) {
        return UserEntity.of(
                id,
                username,
                "password",
                UserRole.USER,
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())
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