package com.project.sns.repository;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UpVoteEntity;
import com.project.sns.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpVoteEntityRepository extends JpaRepository<UpVoteEntity, Long> {
    Optional<UpVoteEntity> findByUserEntityAndPostEntity(UserEntity userEntity, PostEntity post);
}
