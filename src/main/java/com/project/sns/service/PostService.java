package com.project.sns.service;

import com.project.sns.domain.CommentEntity;
import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UpVoteEntity;
import com.project.sns.domain.UserEntity;
import com.project.sns.dto.entity.PostDto;
import com.project.sns.dto.request.CommentCreateRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.CommentEntityRepository;
import com.project.sns.repository.PostRepository;
import com.project.sns.repository.UpVoteEntityRepository;
import com.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserEntityRepository userEntityRepository;
    private final UpVoteEntityRepository upVoteEntityRepository;
    private final CommentEntityRepository commentEntityRepository;

    public Page<PostDto> fetchAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostDto::from);
    }

    public Page<PostDto> fetchMyPosts(Pageable pageable, String userName) {
        /*
        존재하는 유저인지 확인
         */
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));

        /*
        유저의 모든 포스트들 가져오기
         */
        return postRepository.findAllByUserEntity(pageable, userEntity)
                .map(PostDto::from);
    }

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

    @Transactional
    public void upvote(String userName, Long id) {
        //존재하는 유저인지 확인
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));

        // Post 찾기
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));

        // UpVote 찾기 -> UserEntity와 PostEntity 로 존재하는지 찾기 -> 존재하지 않으면 새로 생성해서 반환
        UpVoteEntity upVoteEntity = upVoteEntityRepository.findByUserEntityAndPostEntity(userEntity, post)
                .orElse(UpVoteEntity.of(userEntity, post, false));

        upVoteEntity.updateUpVote();
        upVoteEntityRepository.save(upVoteEntity);
    }

    public Long fetchUpVotesCount(Long id) {
        // Post 찾기
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));

        // Upvote 갯수 찾아서 반환
        return upVoteEntityRepository.findByPostEntityAndUpVoted(post, true);
    }

    @Transactional
    public void addComment(CommentCreateRequest commentCreateRequest, Long id, String userName) {
        //존재하는 유저인지 확인
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));

        // Post 찾기
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));

        CommentEntity commentEntity = CommentEntity.of(commentCreateRequest.getComment(), userEntity, postEntity);
        commentEntityRepository.save(commentEntity);
    }

    private boolean userDoesNotMatch(String userName, PostEntity post) {
        return !userName.equals(post.getUserEntity().getUsername());
    }
}
