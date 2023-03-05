package com.project.sns.repository;

import com.project.sns.domain.CommentEntity;
import com.project.sns.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

    Page<CommentEntity> findAllByPostEntity(PostEntity postEntity, Pageable pageable);

    @Modifying // Queries that require a `@Modifying` annotation include INSERT, UPDATE, DELETE, and DDL statements.
    @Query("DELETE FROM CommentEntity c WHERE c.postEntity = :postEntity")
    void deleteAllByPostEntity(PostEntity postEntity);
}
