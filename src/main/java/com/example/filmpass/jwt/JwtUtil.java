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

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    //JWT에서 사용자 이름 추출
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    //JWT 클레임에서 만료 날짜
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    //특정 클레임 추출 시 타입 자유롭게 지정해서 사용
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    //모든 클레임 추출 시 사용하기
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    //만료 검사
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    //엑세스 토큰 생성
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 1, false); // 1시간 동안 유효한 액세스 토큰
    }
    //리프레시 토큰 생성
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, 30, true); // 30일 동안 유효한 리프레시 토큰
    }
    //JWT 생성
    private String createToken(Map<String, Object> claims, String subject, int duration, boolean isRefreshToken) {
        long expirationTime;
        if (isRefreshToken) {
            expirationTime = 1000 * 60 * 60 * 24 * duration; // 일단위로 됨(리프레시 토큰)
        } else {
            expirationTime = 1000 * 60 * duration; // 시간 단위(액세스 토큰)
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    //JWT가 특정 사용자 이름과 일치하여 만료되었는지 검사하는 기능
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
