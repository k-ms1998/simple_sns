package com.project.sns.service;

import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.dto.entity.NotificationDto;
import com.project.sns.dto.entity.UserDto;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.NotificationEntityRepository;
import com.project.sns.repository.UserDtoCacheRepository;
import com.project.sns.repository.UserEntityRepository;
import com.project.sns.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;
    private final NotificationEntityRepository notificationEntityRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserDtoCacheRepository userDtoCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expire-time}")
    private Long expireTime;

    @Transactional
    public UserEntity join(String username, String password) {
        if (areParametersInValid(username, password)) {
            throw new SnsApplicationException(ErrorCode.EMPTY_PARAMETERS, null);
        }
        /*
        이미 username 이 존재할때
         */
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("\'%s\' already exists.", username));
        });

        /*
        회원가입 진행
         */
        return userEntityRepository.save(UserEntity.of(username, passwordEncoder.encode(password), UserRole.USER));
    }

    public String login(String username, String password) {
        if (areParametersInValid(username, password)) {
            throw new SnsApplicationException(ErrorCode.EMPTY_PARAMETERS, null);
        }

        /*
        loadByUsername 으로 유저 가져오기
        1. 캐시에 있는지 확인하고, 있으면 가져오기
        2. 캐시에 없으면, DB에 접근해서 유저 가져오기
        3. DB에도 없으면 예외 처리
         */
        UserDto userDto = loadUserByUsername(username);
        
        /*
        비밀번호 체크
            -> 성공시 토큰 생성
         */
        if (isCorrectPassword(userDto.getPassword(), password, passwordEncoder)) {
            /*
            유저 정보 캐싱하기
             */
            userDtoCacheRepository.setUserDto(userDto);
            
            /*
            토큰 생성
             */
            return JwtTokenUtils.generateToken(username, secretKey, expireTime);
        }else{
            throw new SnsApplicationException(ErrorCode.INCORRECT_PASSWORD, "");
        }
    }

    /*
    1. 캐시에서 해당 username의 유저가 있는지 확인
    2. 없으면 DB에 접근해서 가져오기
    3. DB에도 없으면 예외 처리
     */
    public UserDto loadUserByUsername(String username) {
        return userDtoCacheRepository.getUserDto(username)
                .orElseGet(() -> userEntityRepository.findByUsername(username).map(UserDto::from)
                        .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, String.format("User \'%s\' doesn't exist.", username))));

    }

    private static boolean isCorrectPassword(String targetPassword, String password, BCryptPasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(password, targetPassword);
    }

    private boolean areParametersInValid(String username, String password) {
        return (username == null || username.isBlank() || password == null || password.isBlank());
    }

    public Page<NotificationDto> fetchNotifications(Pageable pageable, Long userId) {

        return notificationEntityRepository.findAllByUserEntityId(userId, pageable)
                .map(NotificationDto::fromEntity);
    }

    private UserEntity getUserEntityOrThrowException(String userName) {
        UserEntity userEntity = userEntityRepository.findByUsername(userName)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, "Check User."));
        return userEntity;
    }
}
