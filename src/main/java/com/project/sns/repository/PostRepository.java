package com.project.sns.repository;

import com.project.sns.domain.PostEntity;
import com.project.sns.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Page<PostEntity> findAllByUserEntity(Pageable pageable, UserEntity userEntity);
}
