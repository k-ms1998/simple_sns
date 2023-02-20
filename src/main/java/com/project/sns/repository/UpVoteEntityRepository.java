package com.project.sns.repository;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UpVoteEntity;
import com.project.sns.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UpVoteEntityRepository extends JpaRepository<UpVoteEntity, Long> {
    
    Optional<UpVoteEntity> findByUserEntityAndPostEntity(UserEntity userEntity, PostEntity post);

    @Query("SELECT COUNT(*) FROM UpVoteEntity u WHERE u.postEntity=:postEntity AND u.upVoted=:upVoted")
    Long findByPostEntityAndUpVoted(PostEntity postEntity, boolean upVoted);
}
