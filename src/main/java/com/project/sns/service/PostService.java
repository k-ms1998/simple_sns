package com.project.sns.service;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserEntityRepository userEntityRepository;

    @Transactional
    public void create(String title, String body, String userName) {
        /*
        존재하는 유저인지 확인
         */
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));


        /*
        존재할 경우, 포스트 작성
         */
        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        postRepository.save(postEntity);
    }
}
