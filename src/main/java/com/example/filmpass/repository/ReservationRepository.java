package com.example.filmpass.repository;

import com.example.filmpass.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findBySeatSeatId(Long seatId);
    Reservation findByReserveId(Long reserveId);
}
