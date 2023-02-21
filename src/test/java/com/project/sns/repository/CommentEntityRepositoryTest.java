package com.project.sns.repository;

import com.project.sns.domain.CommentEntity;
import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UpVoteEntity;
import com.project.sns.domain.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class CommentEntityRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentEntityRepository commentEntityRepository;


    @DisplayName("[CommentEntityRepository][findAllByPostEntity] ")
    @Test
    void givenTestData_whenCallingFindByPostEntityAndUpVoted_thenSuccess() throws Exception {
        // Given
        UserEntity userA = UserEntity.of("usernameA", "password");
        PostEntity postA = PostEntity.of("titleA", "body", userA);
        PostEntity postB = PostEntity.of("titleB", "body", userA);

        CommentEntity commentA = CommentEntity.of("commentA", userA, postA);
        CommentEntity commentB = CommentEntity.of("commentB", userA, postB);

        userEntityRepository.saveAllAndFlush(List.of(userA));
        postRepository.saveAllAndFlush(List.of(postA, postB));
        commentEntityRepository.saveAllAndFlush(List.of(commentA, commentB));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<CommentEntity> result = commentEntityRepository.findAllByPostEntity(postA, pageable);

        // Then
        Assertions.assertThat(result.getContent().size()).isEqualTo(1);
    }
}