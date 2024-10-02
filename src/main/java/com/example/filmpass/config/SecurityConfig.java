package com.example.filmpass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (개발 중일 때만)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/member/signup", "/member/login").permitAll() // 회원가입 및 로그인 엔드포인트 접근 허용
                        .anyRequest().authenticated() // 다른 모든 요청은 인증 필요, 로그인한 사람?만 가능하다는 느낌입니다
                );

        return http.build();
    }
}

