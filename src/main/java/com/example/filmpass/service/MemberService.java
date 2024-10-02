package com.example.filmpass.service;

import com.example.filmpass.dto.MemberDTO;
import com.example.filmpass.entity.Member;
import com.example.filmpass.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //회원가입 - 이메일, 패스워드, 번호, 프로필 사진 입력
    public Boolean joinProcess(MemberDTO memberDTO){
        String email = memberDTO.getEmail();
        String password = memberDTO.getPassword();
        String number = memberDTO.getNumber();
        String image = memberDTO.getImage();

        if(memberRepository.existsByEmail(email)){
            return false;
        }
        Member member = memberDTO.toEntity();
        //비밀번호 암호화
        member.setPassword(bCryptPasswordEncoder.encode(password));
        //회원가입한 사람에게 USER권한 부여
        member.setRole("ROLE_USER");
        //회원가입 정보 DB 저장
        memberRepository.save(member);
        return true;
    }
}
