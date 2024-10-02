package com.example.filmpass.service;

import com.example.filmpass.dto.MemberSignupDto; // 변경된 DTO 가져오기
import com.example.filmpass.entity.Member;
import com.example.filmpass.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //PasswordEncoder의미는 BCryptPasswordEncoder를 의미합니다

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(MemberSignupDto memberSignupDto) { // DTO 변경
        Member member = new Member();
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        member.setId(memberSignupDto.getId());
        member.setEmail(memberSignupDto.getEmail());
        member.setNumber(memberSignupDto.getNumber());

        // 기본값 처리
        if (memberSignupDto.getImage() == null || memberSignupDto.getImage().isEmpty()) {
            member.setImage("default_image.png"); // 기본 설저ㅓㅇ
        } else {
            member.setImage(memberSignupDto.getImage());
        }

        if (memberSignupDto.getRole() == null || memberSignupDto.getRole().isEmpty()) {
            member.setRole("USER"); // 기본 설정
        } else {
            member.setRole(memberSignupDto.getRole());
        }

        memberRepository.save(member);
    }
}
