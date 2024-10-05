package com.example.filmpass.repository;

import com.example.filmpass.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Seat findBySeatRowAndSeatColAndCinemaCinemaId(int seatRow, int seatCol, Long cinemaId);

    Optional<Boolean> existsByCinemaCinemaId(Long cinemaId);

    List<Seat> findByCinemaCinemaId(Long cinemaId);
}
