package com.example.filmpass.service;

import com.example.filmpass.dto.MemberSignupDto;
import com.example.filmpass.entity.Member;
import com.example.filmpass.jwt.JwtUtil; // JwtUtil 추가
import com.example.filmpass.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Log4j2
@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; // JwtUtil 추가


    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        // 초기화
    }
    //사용자 정보 가져오기
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(member.getId(), member.getPassword(), new ArrayList<>());
    }
    //회원가입
    public void signup(MemberSignupDto memberSignupDto) {
        Member member = new Member();
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        member.setId(memberSignupDto.getId());
        member.setEmail(memberSignupDto.getEmail());
        member.setNumber(memberSignupDto.getNumber());

        // 기본 이미지, 권한
        if (memberSignupDto.getImage() == null || memberSignupDto.getImage().isEmpty()) {
            member.setImage("default_image.png");
        } else {
            member.setImage(memberSignupDto.getImage());
        }

        if (memberSignupDto.getRole() == null || memberSignupDto.getRole().isEmpty()) {
            member.setRole("USER");
        } else {
            member.setRole(memberSignupDto.getRole());
        }

        memberRepository.save(member);
    }

    // 멤버 찾기
    public Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
    public void updateMemberCache(Member member) {
        // 캐시를 업데이트하는 로직
    }

    // 로그인
    public Map<String, String> login(String id, String password) {
        // 사용자 인증 기능
        UserDetails userDetails = loadUserByUsername(id);
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            String token = jwtUtil.generateToken(id);
            String refreshToken = jwtUtil.generateRefreshToken(id);

            // 리프레시 토큰을 DB에 저장
            Member member = memberRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            member.setRefreshToken(refreshToken); // 리프레시 토큰 설정
            memberRepository.save(member); // DB에 업데이트

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", token);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        } else {
            throw new RuntimeException("Login faileddd");
        }
    }

    //이미지 업뎃
    public void updateProfileImage(String id, String newImage) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        member.setImage(newImage); // 새로운 이미지로 업데이트
        memberRepository.save(member); // 변경 사항 저장
    }



}
