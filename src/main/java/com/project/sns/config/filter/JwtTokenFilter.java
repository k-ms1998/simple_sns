package com.project.sns.config.filter;

import com.project.sns.dto.entity.UserDto;
import com.project.sns.service.UserEntityService;
import com.project.sns.util.JwtTokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final String key;
    private final UserEntityService userEntityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*
        Header 가져오기
         */
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isBlank()) {
            log.error("Empty Header.");
            filterChain.doFilter(request, response);
            return;
        }
        if (!header.startsWith("Bearer ")) {
            log.error("Error occurred while fetching header.");
            filterChain.doFilter(request, response);
            return;
        }

        /*
        Header에서 토큰값 가져오기
         */
        try {
            final String token = header.split(" ")[1].trim(); // 'Bearer {token}' 으로 저장되어 있음
            // token 이 expired 됐는지 확인
            if(JwtTokenUtils.isExpired(token, key)){
                log.error("Token Expired.");
                return;
            }

            // token 값에서 username 추출하기
            String username = JwtTokenUtils.fetchUsername(token, key);

            // username 의 userEntity 가 존재하는지 확인
            UserDto userDto = userEntityService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDto,
                    null,
                    userDto.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (RuntimeException e) {
            log.error("Error occurred while fetching token.");
            filterChain.doFilter(request, response);

            return;
        }
    }
}
