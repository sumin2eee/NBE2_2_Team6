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

import java.util.Map;

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
    /*// 로그인 POST 요청
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDto memberLoginDto, HttpServletResponse response) {
        // 로그인 처리 (AuthenticationManager를 이용해 인증 시도)
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginDto.getId(), memberLoginDto.getPassword()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password"); // 401 Unauthorized 응답
        }

        // 로그인 시 JWT 액세스 토큰 바로 생성 그리고 밑에
        String jwtToken = jwtUtil.generateToken(memberLoginDto.getId());

        // JWT 토큰을 쿠키에 저장//이제 서버에 요청시 쿠키에 토큰 정보들이 자동으로 포함됨
        Cookie jwtCookie = new Cookie("token", jwtToken);
        jwtCookie.setHttpOnly(true); //HttpOnly하면 Js로 수정불가해서 추가함
        jwtCookie.setMaxAge(60 * 60 * 1); // 1시간 유효
        response.addCookie(jwtCookie); // HttpServletResponse를 사용해 쿠키 추가

        return ResponseEntity.ok("Login successful"); // 200 OK 응답
    }
}*/
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDto memberLoginDto, HttpServletResponse response) {
        // 로그인 post 요청
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginDto.getId(), memberLoginDto.getPassword()));

            Map<String, String> tokens = memberService.login(memberLoginDto.getId(), memberLoginDto.getPassword());
            String jwtToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");

            // JWT 액세스 토큰을 쿠키에 저장//이제 서버에 요청시 쿠키에 토큰 정보들이 자동으로 포함됨
            Cookie jwtCookie = new Cookie("token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(60 * 60 * 1);
            response.addCookie(jwtCookie);
            // JWT 리프레쉬 토큰 쿠키에 저장
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok("Login successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
}


