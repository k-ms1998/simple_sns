package com.project.sns.repository;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UpVoteEntity;
import com.project.sns.domain.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class UpVoteEntityRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserEntityRepository userEntityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UpVoteEntityRepository upVoteEntityRepository;

    @DisplayName("[UpVoteEntityRepository][findByPostEntityAndUpVoted] ")
    @Test
    void givenTestData_whenCallingFindByPostEntityAndUpVoted_thenSuccess() throws Exception {
        // Given
        UserEntity userA = UserEntity.of("usernameA", "password");
        UserEntity userB = UserEntity.of("usernameB", "password");
        UserEntity userC = UserEntity.of("usernameC", "password");
        PostEntity post = PostEntity.of("title", "body", userA);

        UpVoteEntity upVoteA = UpVoteEntity.of(userA, post, true);
        UpVoteEntity upVoteB = UpVoteEntity.of(userB, post, true);
        UpVoteEntity upVoteC = UpVoteEntity.of(userC, post, false);

        userEntityRepository.saveAllAndFlush(List.of(userA, userB, userC));
        postRepository.saveAndFlush(post);
        upVoteEntityRepository.saveAllAndFlush(List.of(upVoteA, upVoteB, upVoteC));

        // When
        Long result = upVoteEntityRepository.findByPostEntityAndUpVoted(post, true);
        List<UpVoteEntity> all = upVoteEntityRepository.findAll();
        // Then
        Assertions.assertThat(result).isEqualTo(2L);
    }
}