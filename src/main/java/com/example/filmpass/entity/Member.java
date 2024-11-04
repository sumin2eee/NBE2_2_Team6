package com.example.filmpass.entity;

import com.example.filmpass.entity.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id") //  기본 키
    private Long memberId;

    @NotNull
    @Size(min = 4)
    private String password; // 비밀번호

    @Column(unique = true)
    private String id; // 사용자 ID

    @Column(unique = true)
    private String email; // 이메일

    @NotNull
    @Size(min = 10, max = 15)
    @Column(unique = true)
    private String number; // 폰번호

    private String image; // 프로필 사진
    private String role; // 권한

    private String refreshToken; // 재생성 토큰

    @Column(name = "name")
    private String name; // 사용자 이름 추가


    @Getter
    @Setter
    private Long point; // 포인트 필드 추가

}
