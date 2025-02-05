package com.mandamong.api.member.filter;

import com.mandamong.api.member.util.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private static final String TOKEN_HEADER = "Authorization";
    private final static String BEARER_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = "";
        String authorizationHeader;

        // 요청 헤더의 Authorization 키의 값 조회
        authorizationHeader = request.getHeader(TOKEN_HEADER);

        // 가져온 값에서 접두사 제거
       if(authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
          token = authorizationHeader.substring(BEARER_PREFIX.length());
       }

        // 가져온 토큰이 유효한지 확인하고, 유효한 때는 인증 정보 설정
        try{
            // AccessToken 유효성 확인
            if (tokenProvider.validateAccessToken(token)){
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ExpiredJwtException e) {
            // AccessToken이 만료되었으면 RefreshToken으로 갱신 시도
            Long memberId = e.getClaims().get("memberId", Long.class);
            // Redis에서 RefreshToken 꺼내기
            String refreshToken = tokenProvider.getRefreshTokenFromRedis(REFRESH_TOKEN_PREFIX + memberId);

            if(tokenProvider.validateRefreshToken(refreshToken)){
                String newAccessToken = tokenProvider.makeAccessToken(memberId);
                Authentication authentication = tokenProvider.getAuthentication(newAccessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 새 AccessToken을 응답 헤더에 추가
                response.setHeader("New-Access-Token", newAccessToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
