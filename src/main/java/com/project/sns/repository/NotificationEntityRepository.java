package com.project.sns.repository;

import com.project.sns.domain.NotificationEntity;
import com.project.sns.domain.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEntityRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findAllByUserEntity(UserEntity userEntity, Pageable pageable);
}
