package com.project.sns.service;

import com.project.sns.config.SpringSecurityConfig;
import com.project.sns.domain.UserEntity;
import com.project.sns.domain.enums.UserRole;
import com.project.sns.dto.request.UserJoinRequest;
import com.project.sns.dto.request.UserLoginRequest;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@SpringBootTest
@Import(value = {SpringSecurityConfig.class})
class UserEntityServiceTest {

    @Autowired
    private UserEntityService sut;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("[Service] {join()} Given Correct Parameters - Joining User - Success")
    void givenCorrectParameters_whenJoining_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserEntity userEntity = createOptionalUserEntity(username, password, passwordEncoder).get();

        given(userEntityRepository.findByUsername(username)).willReturn(Optional.empty());
        given(userEntityRepository.save(any(UserEntity.class))).willReturn(userEntity);

        // When && Then
        Assertions.assertDoesNotThrow(() -> sut.join(username, password));
        then(userEntityRepository).should().findByUsername(username);
        then(userEntityRepository).should().save(any(UserEntity.class));
    }

    @Test
    @DisplayName("[Service] {join()} Given Existing Username - Joining User - Fails")
    void givenExistingUsername_whenJoining_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        Optional<UserEntity> userEntity = createOptionalUserEntity(username, password, passwordEncoder);
        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);

        // When && Then
        Assertions.assertThrows(SnsApplicationException.class, () -> sut.join(username, password));
        then(userEntityRepository).should().findByUsername(username);

    }

    @Test
    @DisplayName("[Service] {login()} Given Correct Parameters - Logging In User - Success")
    void givenCorrectParameters_whenLoggingIn_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        Optional<UserEntity> userEntity = createOptionalUserEntity(username, password, passwordEncoder);
        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);

        // When && Then
        Assertions.assertDoesNotThrow(() -> sut.login(username, password));
        then(userEntityRepository).should().findByUsername(username);

    }

    @Test
    @DisplayName("[Service] {login()} Given Non-Existing Username - Logging In User - Fails")
    void givenNonExistingUsername_whenLoggingIn_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        UserLoginRequest request = createUserLoginRequest(username, password);
        given(userEntityRepository.findByUsername(username)).willReturn(Optional.empty());

        // When && Then
        Assertions.assertThrows(SnsApplicationException.class, () -> sut.login(username, password));
        then(userEntityRepository).should().findByUsername(username);

    }

    @Test
    @DisplayName("[Service] {login()} Given Incorrect Password - Logging In User - Fails")
    void givenIncorrectPassword_whenLoggingIn_thenSuccess() throws Exception {
        // Given
        String username = "kms";
        String password = "password";
        String wrongPassword = "wrongPassword";
        Optional<UserEntity> userEntity = createOptionalUserEntity(username, password, passwordEncoder);
        given(userEntityRepository.findByUsername(username)).willReturn(userEntity);

        // When && Then
        Assertions.assertThrows(SnsApplicationException.class, () -> sut.login(username, wrongPassword));
        then(userEntityRepository).should().findByUsername(username);

    }

    private static UserJoinRequest createUserJoinRequest(String username, String password) {
        return UserJoinRequest.of(username, password);
    }

    private static UserLoginRequest createUserLoginRequest(String username, String password) {
        return UserLoginRequest.of(username, password);
    }

    private static Optional<UserEntity> createOptionalUserEntity(String username, String password, BCryptPasswordEncoder passwordEncoder) {
        return Optional.of(UserEntity.of(
                        1L,
                        username,
                        passwordEncoder.encode(password),
                        UserRole.USER,
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now())
                )
        );
    }

}