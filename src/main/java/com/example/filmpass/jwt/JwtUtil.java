package com.example.filmpass.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.time.ZonedDateTime;
import java.time.ZoneId;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // JWT에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // JWT 클레임에서 만료 날짜 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 특정 클레임 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 모든 클레임 추출
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
    }

    // 토큰 만료 여부 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 엑세스 토큰 생성
    public String generateToken(String username) {
        long currentTimeMillis = System.currentTimeMillis();

        // 현재 시간을 KST 기준으로 출력
        ZonedDateTime nowKST = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        System.out.println("Current Time (KST - JWT Generation): " + nowKST);
        System.out.println("JWT 만료 시간 (KST): " + nowKST.plusHours(1)); // 만료 시간을 1시간 후로 출력

        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 1, false); // 1시간 동안 유효한 액세스 토큰
    }


    // 리프레시 토큰 생성 (30일 유효)
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 30, true);
    }

    // JWT 생성 메서드
    private String createToken(Map<String, Object> claims, String subject, int duration, boolean isRefreshToken) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")); // 한국 시간대(KST) 사용
        Date issuedAt = Date.from(now.toInstant()); // 발급 시간
        Date expiration = Date.from(now.plusHours(duration).toInstant()); // 만료 시간 (시간 단위)

        if (isRefreshToken) {
            expiration = Date.from(now.plusDays(duration).toInstant()); // 리프레시 토큰은 일 단위
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT 유효성 검사
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
