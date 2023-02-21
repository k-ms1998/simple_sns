package com.project.sns.repository;

import com.project.sns.domain.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {
}
