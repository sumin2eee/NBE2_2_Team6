package com.example.filmpass.repository;

import com.example.filmpass.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(String id); // 사용자 ID로 회원 찾기
}
