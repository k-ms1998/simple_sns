package com.project.sns.service;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.dto.entity.PostDto;
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

    @Transactional
    public PostDto update(String title, String body, String userName, Long postId) {
        // 존재하는 유저인지 확인
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));

        // Post 가져오기
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));

        // 가져온 Post의 작성자와 업데이트 할려는 유저가 동일한지 확인
        if(userDoesNotMatch(userName, post)){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, "User does not match.");
        }

        // Post 업데이트 후 저장하기
        post.updatePost(title, body);
        return postRepository.save(post).toDto();
    }

    @Transactional
    public void delete(String userName, Long postId) {
        //존재하는 유저인지 확인
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));

        // Post 찾기
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));

        // 가져온 Post의 작성자와 업데이트 할려는 유저가 동일한지 확인
        if(userDoesNotMatch(userName, post)){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, "User does not match.");
        }

        // Post 삭제
        postRepository.delete(post);

    }

    private boolean userDoesNotMatch(String userName, PostEntity post) {
        return !userName.equals(post.getUserEntity().getUsername());
    }
}
