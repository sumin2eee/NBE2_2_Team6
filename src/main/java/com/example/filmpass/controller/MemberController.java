package com.example.filmpass.controller;

import com.example.filmpass.dto.MemberLoginDto;
import com.example.filmpass.dto.MemberSignupDto;
import com.example.filmpass.dto.MemberUpdateDto; // 추가
import com.example.filmpass.jwt.JwtUtil;
import com.example.filmpass.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Log4j2
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PutMapping("/profile-image")
    public ResponseEntity<String> updateProfileImage(@RequestBody MemberUpdateDto memberUpdateDto, HttpServletRequest request) {
        // JWT에서 사용자 ID 추출
        String jwt = request.getHeader("Authorization").substring(7); // "Bearer " 부분 제거
        String username = jwtUtil.extractUsername(jwt); // 사용자 ID 추출

        // 새로운 이미지 URL 가져오기
        String newImage = memberUpdateDto.getImage(); // DTO에서 가져옴

        // 프로필 이미지 업데이트
        memberService.updateProfileImage(username, newImage);

        return ResponseEntity.ok("Profile image updated successfully");
    }

    // 회원가입 POST 요청
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody MemberSignupDto memberSignupDto) {
        memberService.signup(memberSignupDto); // 회원가입 처리
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully"); // 201 Created 응답
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginDto memberLoginDto, HttpServletResponse response) {
        // 로그인 post 요청
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(memberLoginDto.getId(), memberLoginDto.getPassword()));

            Map<String, String> tokens = memberService.login(memberLoginDto.getId(), memberLoginDto.getPassword());
            String jwtToken = tokens.get("accessToken");
            String refreshToken = tokens.get("refreshToken");

            // JWT 액세스 토큰을 쿠키에 저장
            Cookie jwtCookie = new Cookie("token", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setMaxAge(60 * 60 * 1);
            response.addCookie(jwtCookie);
            // JWT 리프레쉬 토큰 쿠키에 저장
            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge(60 * 60 * 24 * 30);
            response.addCookie(refreshCookie);
            log.info("JWT token: " + jwtToken);
            return ResponseEntity.ok("Login successful");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }
}
