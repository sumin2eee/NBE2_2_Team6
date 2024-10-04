package com.example.filmpass.dto;

import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Movie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaMovieDto {
    private Long movieId;

    private Long cinemaId;

    private LocalDate screenDate;

    private LocalTime screenTime;



    public CinemaMovieDto(Long id, CinemaMovie cinemaMovie) {
        this.movieId = id;
        this.cinemaId = cinemaMovie.getCinema().getCinemaId();
        this.screenDate = cinemaMovie.getScreenDate();
        this.screenTime = cinemaMovie.getScreenTime();
    }

    public CinemaMovie toEntity(Movie movie, Cinema cinema){
        return CinemaMovie.builder()
                .movie(movie)
                .cinema(cinema)
                .screenDate(screenDate)
                .screenTime(screenTime).build();
    }
}
