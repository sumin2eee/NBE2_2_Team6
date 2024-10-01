package com.example.filmpass.repository;

import com.example.filmpass.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByMovieCd(String movieCd);
    Optional<Movie> findByMovieName(String movieName);
    Movie findByMovieCd(String movieCd);
}
