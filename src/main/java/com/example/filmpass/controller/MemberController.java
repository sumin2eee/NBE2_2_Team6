package com.example.filmpass.controller;

import com.example.filmpass.dto.MemberLoginDto;
import com.example.filmpass.dto.MemberSignupDto;
import com.example.filmpass.jwt.JwtUtil;
import com.example.filmpass.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // 회원가입 POST 요청
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody MemberSignupDto memberSignupDto) {
        memberService.signup(memberSignupDto); // 회원가입 처리
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"); // 201 Created 응답
    }

    // 로그인 POST 요청
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDto memberLoginDto, HttpServletResponse response) {
        // 로그인 처리 (AuthenticationManager를 이용해 인증 시도)
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginDto.getId(), memberLoginDto.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"); // 401 Unauthorized 응답
        }

        // JWT 토큰 생성
        String jwtToken = jwtUtil.generateToken(memberLoginDto.getId());

        // JWT 토큰을 쿠키에 저장
        Cookie jwtCookie = new Cookie("token", jwtToken);
        jwtCookie.setHttpOnly(true); // 보안 강화
        jwtCookie.setMaxAge(60 * 60 * 10); // 10시간 유효
        response.addCookie(jwtCookie); // HttpServletResponse를 사용해 쿠키 추가

        return ResponseEntity.ok("Login successful"); // 200 OK 응답
    }
}
