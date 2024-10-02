package com.example.filmpass.controller;

import com.example.filmpass.dto.MemberLoginDto;
import com.example.filmpass.dto.MemberSignupDto;  // Signup용 DTO 추가
import com.example.filmpass.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    // 회원가입 폼 GET 요청
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("memberSignupDto", new MemberSignupDto()); // Signup용 DTO
        return "signupform";  // 회원가입 폼 템플릿
    }

    // 로그인 폼 GET 요청
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("memberLoginDto", new MemberLoginDto()); // Login용 DTO
        return "loginform"; // 로그인 폼 템플릿
    }

    // 회원가입 POST 요청
    @PostMapping("/signup")
    public String signup(@Valid MemberSignupDto memberSignupDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || memberSignupDto.getPassword() == null) {
            return "signupform"; // 오류가 있을 경우 폼으로 돌아감
        }

        memberService.signup(memberSignupDto); // 회원가입 처리
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }

    // 로그인 POST 요청
    @PostMapping("/login")
    public String login(@Valid MemberLoginDto memberLoginDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "loginform"; // 오류 시 로그인 폼으로 돌아감
        }

        // 로그인 처리 로직 필요 (추가 예정), 일단 회원가입 먼저 실험 중(이 코드: jwt 적용 전 상태)
        // db로 회원가입 데이터가 들어가는 것 확인된 상태, 비번도 해싱됨
        return "redirect:/"; // 메인 페이지로 리다이렉트
    }
}
