package com.example.filmpass.repository;

import com.example.filmpass.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByOrderNo(String orderNo);  // orderNo로 Payment 찾기

   Optional<Payment> findTopByOrderByCreatedTsDesc();
}
