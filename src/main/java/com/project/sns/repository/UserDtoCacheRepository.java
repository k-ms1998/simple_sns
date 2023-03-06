package com.project.sns.repository;

import com.project.sns.dto.entity.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * UserDto를 Redis에 캐싱하기
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDtoCacheRepository {

    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;
    private final static Duration USER_CACHE_TTL = Duration.ofDays(1); // TTL = Time To Live

    public Optional<UserDto> getUserDto(String username) {
        String key = getKey(username);
        UserDto userDto = userDtoRedisTemplate.opsForValue().get(key);
        log.info("Get UserDto from Redis {},{}", key, userDto);

        return Optional.ofNullable(userDto);
    }

    public void setUserDto(UserDto userDto) {
        String key = getKey(userDto.getUsername());
        log.info("Set UserDto to Redis {},{}", key, userDto);
        userDtoRedisTemplate.opsForValue().set(key, userDto, USER_CACHE_TTL); // cache가 저장되고, USER_CACHE_TTL 이후에는 삭제됨
    }

    private String getKey(String username) {
        return "USER:"+username; //  단순히 username 으로만 key 값을 정의할 경우, 다른 데이터를 캐싱할때 username을 중복으로 key에서 사용 X & 어떤 데이터에 대한 케싱인지 알 수 없음 -> 앞에 prefix를 붙여줌
    }
}
