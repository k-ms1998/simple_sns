package com.project.sns.repository;

import com.project.sns.domain.CommentEntity;
import com.project.sns.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostEntity(PostEntity postEntity, Pageable pageable);
}
