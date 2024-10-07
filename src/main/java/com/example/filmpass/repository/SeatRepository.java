package com.example.filmpass.repository;

import com.example.filmpass.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
//    Seat findBySeatRowAndSeatColAndCinemaCinemaId(int seatRow, int seatCol, Long cinemaId);

//    Optional<Boolean> existsByCinemaCinemaId(Long cinemaId);

    List<Seat> findByCinemaMovieCinemaMovieId(Long cinemaMovieId);

//    Optional<Boolean> existsByCinemaMovie_CinemaMovieId(Long cinemaMovieId);

    Seat findBySeatRowAndSeatColAndCinemaMovieCinemaMovieId(int rows, int cols, Long cinemaMovieId);

    Optional<Boolean> existsByCinemaMovie_CinemaMovieIdAndCinema_cinemaId(Long cinemaMovieId, Long cinemaId);
}
