package com.project.sns.config;

import com.project.sns.config.filter.JwtTokenFilter;
import com.project.sns.exception.CustomAuthenticationEntryPoint;
import com.project.sns.service.UserEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserEntityService userEntityService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                "/",
                "/users/join",
                "/users/login",
                "/posts",
                "/posts/upvote/{id}/count"
        ).requestMatchers(HttpMethod.GET,
                "/posts/comment/{id}");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtTokenFilter(secretKey, userEntityService), UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 이전에 JwtTokenFilter 적용
                .build();

    }

}