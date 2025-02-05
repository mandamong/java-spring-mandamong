package com.mandamong.api.member.service;

import org.hibernate.mapping.Any;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 레디스에 리프레시 토큰 저장
    public void saveRefreshToken(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set("RT:" + memberId, refreshToken, Duration.ofDays(7));
    }

    // 레디스에서 리프레시 토큰 꺼내기
    public String getRefreshToken(Long memberId) {
        return redisTemplate.opsForValue().get("RT:" + memberId);
    }

    // 리프레시 토큰 삭제
    public void deleteRefreshToken(Long memberId) {
        boolean status = redisTemplate.delete("RT:" + memberId);
    }
}
