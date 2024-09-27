package com.example.filmpass.repository;

import com.example.filmpass.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    boolean existsByMovieCd(String movieCd);

    Movie findByMovieCd(String movieCd);
}
