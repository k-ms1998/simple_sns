package com.project.sns.service;

import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.exception.enums.ErrorCode;
import com.project.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserEntityService {

    private final UserEntityRepository userEntityRepository;

    public UserEntity join(String username, String password) {
        if (areParametersInValid(username, password)) {
            throw new SnsApplicationException(ErrorCode.EMPTY_PARAMETERS, null);
        }
        /*
        이미 username 이 존재할때
         */
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATE_USER_NAME, String.format("%s already exists.", username));
        });

        /*
        회원가입 진행
         */
        return userEntityRepository.save(UserEntity.of(username, password, UserRole.USER));
    }

    public String login(String username, String password) {
        if (areParametersInValid(username, password)) {
            throw new SnsApplicationException(ErrorCode.EMPTY_PARAMETERS, null);
        }

        /*
        회원 가입 여부 체크
         */
        UserEntity userEntity = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new SnsApplicationException(ErrorCode.NON_EXISTING_USER, String.format("User %s doesn't exist.", username)));

        /*
        비밀번호 체크
            -> 성공시 토큰 생성
         */
        if (isCorrectPassword(userEntity, password)) {
            return "Logged In";
        }else{
            throw new SnsApplicationException(ErrorCode.INCORRECT_PASSWORD, null);
        }
    }

    private static boolean isCorrectPassword(UserEntity userEntity, String password) {
        return userEntity.getPassword().equals(password);
    }

    private boolean areParametersInValid(String username, String password) {
        return (username == null || username.isBlank() || password == null || password.isBlank());
    }
}