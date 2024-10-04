package com.example.filmpass.repository;

import com.example.filmpass.dto.CinemaMovieDto;
import com.example.filmpass.entity.CinemaMovie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CinemaMovieRepository extends JpaRepository<CinemaMovie, Long> {
    List<CinemaMovie> findByMovie_MovieId(Long movieId);
}
