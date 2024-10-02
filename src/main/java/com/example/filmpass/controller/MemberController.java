package com.example.filmpass.controller;

import com.example.filmpass.dto.MemberDTO;
import com.example.filmpass.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    //회원가입
    //예외처리 추가하기 - 회원가입 실패 , 이미 가입된 유저, 유효성 검사 등
    @PostMapping("/join")
    public String joinProcess(MemberDTO memberDTO){
        if(memberService.joinProcess(memberDTO)){
            log.info(memberDTO);
            return "Registration completed successfully!";
        }
        return "This email is already registered. Redirecting to the login page.";
    }
}