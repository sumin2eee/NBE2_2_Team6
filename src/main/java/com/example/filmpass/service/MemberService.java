package com.example.filmpass.service;

import com.example.filmpass.dto.MemberSignupDto;
import com.example.filmpass.entity.Member;
import com.example.filmpass.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new org.springframework.security.core.userdetails.User(member.getId(), member.getPassword(), new ArrayList<>());
    }

    public void signup(MemberSignupDto memberSignupDto) {
        Member member = new Member();
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));
        member.setId(memberSignupDto.getId());
        member.setEmail(memberSignupDto.getEmail());
        member.setNumber(memberSignupDto.getNumber());

        // Default image and role handling
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
}
