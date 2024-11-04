package com.example.filmpass.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginDto {
    private String id; // 사용자 ID
    private String password; // 비밀번호
}
