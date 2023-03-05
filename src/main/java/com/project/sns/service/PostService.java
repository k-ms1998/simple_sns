package com.project.sns.service;

import com.project.sns.domain.*;
import com.project.sns.domain.enums.NotificationType;
import com.project.sns.dto.entity.CommentDto;
import com.project.sns.dto.entity.PostDto;
import com.project.sns.dto.request.CommentCreateRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.*;
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
    private final NotificationEntityRepository notificationEntityRepository;

    public Page<PostDto> fetchAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable)
                .map(PostDto::from);
    }

    public Page<PostDto> fetchMyPosts(Pageable pageable, String userName) {
        /*
        존재하는 유저인지 확인
         */
        UserEntity userEntity = getUserEntityOrThrowException(userName);

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
        UserEntity userEntity = getUserEntityOrThrowException(userName);


        /*
        존재할 경우, 포스트 작성
         */
        PostEntity postEntity = PostEntity.of(title, body, userEntity);
        postRepository.save(postEntity);
    }

    @Transactional
    public PostDto update(String title, String body, String userName, Long postId) {
        // 존재하는 유저인지 확인
        getUserEntityOrThrowException(userName);

        // Post 가져오기
        PostEntity post = getPostEntityOrThrowException(postId);

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
        getUserEntityOrThrowException(userName);

        // Post 찾기
        PostEntity post = getPostEntityOrThrowException(postId);

        // 가져온 Post의 작성자와 업데이트 할려는 유저가 동일한지 확인
        if(userDoesNotMatch(userName, post)){
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, "User does not match.");
        }
        
        // Post랑 연관된 UpVote 삭제
        upVoteEntityRepository.deleteAllByPostEntity(post);
        
        // Post랑 연관된 Comment 삭제
        commentEntityRepository.deleteAllByPostEntity(post);

        // Post 삭제
        postRepository.delete(post);

    }

    @Transactional
    public void upvote(String userName, Long id) {
        //존재하는 유저인지 확인
        UserEntity userEntity = getUserEntityOrThrowException(userName);

        // Post 찾기
        PostEntity post = getPostEntityOrThrowException(id);

        // UpVote 찾기 -> UserEntity와 PostEntity 로 존재하는지 찾기 -> 존재하지 않으면 새로 생성해서 반환
        UpVoteEntity upVoteEntity = upVoteEntityRepository.findByUserEntityAndPostEntity(userEntity, post)
                .orElse(UpVoteEntity.of(userEntity, post, false));

        upVoteEntity.updateUpVote();
        upVoteEntityRepository.save(upVoteEntity);


        // 좋아요가 눌렀을 경우 && 좋아요 누른 유저와 포스트 작성한 유저가 다를 경우 알림 생성
        if(upVoteEntity.isUpVoted()){
            if(isNotSameUser(userEntity, post)){
                notificationEntityRepository.save(NotificationEntity.of(
                        post.getUserEntity(),
                        NotificationType.NEW_LIKE_ON_POST,
                        NotificationArgs.of(userEntity.getId(), post.getId())
                ));
            }
        }
    }

    public Long fetchUpVotesCount(Long id) {
        // Post 찾기
        PostEntity post = getPostEntityOrThrowException(id);

        // Upvote 갯수 찾아서 반환
        return upVoteEntityRepository.findByPostEntityAndUpVoted(post, true);
    }

    @Transactional
    public void addComment(CommentCreateRequest commentCreateRequest, Long id, String userName) {
        //존재하는 유저인지 확인
        UserEntity userEntity = getUserEntityOrThrowException(userName);

        // Post 찾기
        PostEntity postEntity = getPostEntityOrThrowException(id);

        // 댓글 저장
        CommentEntity commentEntity = CommentEntity.of(commentCreateRequest.getComment(), userEntity, postEntity);
        commentEntityRepository.save(commentEntity);

        /*
        알림 생성:
            -> 댓글을 단 포스트의 주인에게, 현재 로그인한 유저가 댓글을 달았다는 알림 생성
            -> 댓글 작성자랑 포스트 작성자가 다를 경우에만 알림 생성
         */
        if(isNotSameUser(userEntity, postEntity)){
            notificationEntityRepository.save(NotificationEntity.of(
                    postEntity.getUserEntity(),
                    NotificationType.NEW_COMMENT_ON_POST,
                    NotificationArgs.of(userEntity.getId(), postEntity.getId())
            ));
        }
    }

    public Page<CommentDto> fetchAllComments(Long id, Pageable pageable) {
        // Post 찾기
        PostEntity postEntity = getPostEntityOrThrowException(id);

        return commentEntityRepository.findAllByPostEntity(postEntity, pageable)
                .map(CommentDto::fromEntity);

    }

    private UserEntity getUserEntityOrThrowException(String userName) {
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));
        return userEntity;
    }

    private PostEntity getPostEntityOrThrowException(Long id) {
        PostEntity postEntity = postRepository.findById(id)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND));
        return postEntity;
    }

    private boolean userDoesNotMatch(String userName, PostEntity post) {
        return !userName.equals(post.getUserEntity().getUsername());
    }

    private boolean isNotSameUser(UserEntity userEntity, PostEntity postEntity) {
        return !(userEntity.getUsername().equals(postEntity.getUserEntity().getUsername()));
    }
}
