package com.example.filmpass.repository;

import com.example.filmpass.entity.Movie;
import com.example.filmpass.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
   Payment findByOrderNo(String OrderNo);
}
