package com.example.filmpass.repository;

import com.example.filmpass.entity.Payment;
import com.example.filmpass.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

   Optional<Payment> findTopByOrderByCreatedTsDesc();
    Payment findByReservation(Reservation reservation);
}
