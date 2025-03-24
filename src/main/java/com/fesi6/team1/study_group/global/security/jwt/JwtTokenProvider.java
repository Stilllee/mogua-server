package com.fesi6.team1.study_group.global.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60;  // 60분
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일

    // accessToken 생성
    public String createAccessToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())  // 발행 시간 추가
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))  // 1시간 유효
                .setId(UUID.randomUUID().toString())  // 고유한 ID 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // refreshToken 생성
    public String createRefreshToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())  // 발행 시간 추가
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))  // 7일 유효
                .setId(UUID.randomUUID().toString())  // 고유한 ID 추가
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 토큰이 만료됨
            return false;
        } catch (io.jsonwebtoken.JwtException e) {
            // 잘못된 토큰
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // refreshToken 만료됨
            return false;
        } catch (io.jsonwebtoken.JwtException e) {
            // 잘못된 refreshToken
            return false;
        }
    }

    // accessToken에서 사용자 ID 추출
    public Long getUserIdFromAccessToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    // refreshToken에서 사용자 ID 추출
    public Long getUserIdFromRefreshToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }
}