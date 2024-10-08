package com.example.filmpass.util;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[64]; // 256 bits
        secureRandom.nextBytes(key);
        String base64Key = Base64.getEncoder().encodeToString(key);
        System.out.println("Base64 Encoded Secret Key: " + base64Key);
    }
}// Jwt 유틸에 시크릿키 생성용입니다. 필요시 사용하세요~ 필수x
