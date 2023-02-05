package com.project.sns.config;

import com.project.sns.config.filter.JwtTokenFilter;
import com.project.sns.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final UserEntityService userEntityService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations()
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/users/join",
                                "/users/login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenFilter(secretKey, userEntityService), UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 이전에 JwtTokenFilter 적용
                .formLogin()
                .and()
                .build();

    }

}