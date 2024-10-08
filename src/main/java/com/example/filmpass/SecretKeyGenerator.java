package com.example.filmpass;

import java.security.SecureRandom;
import java.util.Base64;

public class SecretKeyGenerator {
    public static void main(String[] args) {
        // 64바이트의 랜덤 바이트 배열 생성
        byte[] randomBytes = new byte[48]; // 48바이트는 Base64로 인코딩 시 약 64문자에 해당
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        // Base64로 인코딩
        String secretKey = Base64.getEncoder().encodeToString(randomBytes);

        System.out.println("Generated Secret Key: " + secretKey);
    }
}
