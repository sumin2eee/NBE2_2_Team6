package com.example.filmpass.repository;

import com.example.filmpass.entity.Reservation;
import com.example.filmpass.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
