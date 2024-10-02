package com.example.filmpass.repository;

import com.example.filmpass.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatRowAndSeatCol(int seatRow, int seatCol);
}
