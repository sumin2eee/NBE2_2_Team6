package com.example.filmpass.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupDto {
    private String id; // 사용자 ID
    private String password; // 비밀번호
    private String email; // 이메일
    private String number; // 전화번호
    private String image; // 프로필 사진
    private String role; // 권한
}
