package com.mandamong.api.member.util;

import com.mandamong.api.member.domain.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    @Value(value = "${jwt.issuer}")
    private String issuer;
    @Value(value = "${jwt.secret.access}")
    private String accessSecretKey;
    @Value(value = "${jwt.secret.refresh}")
    private String refreshSecretKey;

    private final RedisTemplate<String, String> redisTemplate;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 10; // 10분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30; // 30일

    //// JWT 토큰 생성
    // - AccessToken 생성 메서드
    private String makeAccessToken(Date expiry, Member member){
        Date now = new Date();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS512")
                .and()
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .subject(member.getEmail())
                .claim("id",  member.getId())
                .signWith(Keys.hmacShaKeyFor(accessSecretKey.getBytes()), Jwts.SIG.HS512)
                .compact();
    }
    // - RefreshToken 생성 메서드
    private String makeRefreshToken(Date expiry, Member member){
        Date now = new Date();
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .add("alg", "HS512")
                .and()
                .issuer(issuer)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .subject(member.getEmail())
                .claim("id",  member.getId())
                .signWith(Keys.hmacShaKeyFor(refreshSecretKey.getBytes()), Jwts.SIG.HS512)
                .compact();
    }


    //// JWT 토큰 유효성 검증
    // - AccessToken 유효성 검증 메서드
    public boolean validateAccessToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(accessSecretKey.getBytes()))
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    // - RefreshToken 유효성 검증 메서드
    public boolean validateRefreshToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(refreshSecretKey.getBytes()))
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 토큰에서 memberId를 가져오는 메서드
    public Long getMemberId(String token){
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }
    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(refreshSecretKey.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getBody();
    }

    //// Redis에서 RefreshToken을 조회
    public String getRefreshTokenFromRedis(String loginId){
        return redisTemplate.opsForValue().get(loginId);
    }

}
