package com.project.sns.config;

import com.project.sns.dto.entity.UserDto;
import io.lettuce.core.RedisURI;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableRedisRepositories
@RequiredArgsConstructor
public class MyRedisConfiguration {

    private final RedisProperties redisProperties; //prefix="spring.data.redis"=>application.yml 에서 해당 prefix 를 가진 값들을 불러와서 RedisProperties 에 있는 파라미터들로 주입(url, host, username 등)

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisURI redisUrI = RedisURI.create(redisProperties.getUrl());
        RedisConfiguration configuration = LettuceConnectionFactory.createRedisConfiguration(redisUrI);
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration);
        factory.afterPropertiesSet();   // afterPropertiesSet() 를 해줘야 factory 가 initialized = true 로 변경됨

        return factory;
    }

    /**
     * 캐싱은 접근이 자주 일어나고, 변화가 적은 데이터를 캐싱해야 효율적
     * 데이터가 변화가 많으면, 변화 할때마다 DB에 저장된 값을 변경하고, 변경한 값으로 다시 캐싱해줘야 하기 때문에 비효율적
     *
     * 현 스펙으로는, 인증 과정을 거칠때마다 JwtTokenFilter 에서 유저 정보를 확인
     * 유저 정보는 자주 접근하고, 변경되는 경우가 적기 때문에 캐싱해줌
     */
    @Bean
    public RedisTemplate<String, UserDto> userDtoRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<String, UserDto> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<UserDto>(UserDto.class));

        return redisTemplate;
    }

}
