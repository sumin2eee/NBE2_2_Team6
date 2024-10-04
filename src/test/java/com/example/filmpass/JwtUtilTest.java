package com.example.filmpass;

import com.example.filmpass.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testGenerateToken() {
        // 테스트할 사용자 이름
        String username = "testUser";
        // JWT 토큰 생성
        String token = jwtUtil.generateToken(username);

        // 생성된 토큰 출력, jwt.io에서 확인하셔도 됨
        System.out.println("Generated Token: " + token);

        // 토큰 유효성 검증
        boolean isValid = jwtUtil.validateToken(token, username);

        // 토큰이 유효한지 확인
        assertTrue(isValid, "The generated token should be valid.");
    }
} // 검증용 테스트입니다. 토큰 생성에 오류가 있는지 볼 때 사용하세요~
