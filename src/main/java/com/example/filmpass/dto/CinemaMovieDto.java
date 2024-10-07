package com.example.filmpass.dto;

import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long movieId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long cinemaId;

    private Long cinemaMovieId;

    private String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cinemaName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate screenDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime screenTime;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalTime showTime;

    private Movie movie;

    private Cinema cinema;

    public CinemaMovieDto(Long id, Movie movie, LocalDate screenDate, LocalTime screenTime,LocalTime showTime,String title, Cinema cinema){
        this.cinemaMovieId = id;
        this.movie = movie;
        this.screenDate = screenDate;
        this.screenTime = screenTime;
        this.showTime = showTime;
        this.title = title;
        this.cinema = cinema;
    }


    public CinemaMovieDto(Long id, CinemaMovie cinemaMovie) {
        this.cinemaId = cinemaMovie.getCinema().getCinemaId();
        this.title = cinemaMovie.getMovie().getMovieName();
        this.cinemaMovieId = cinemaMovie.getCinemaMovieId();
        this.cinemaName = cinemaMovie.getCinema().getCinemaName();
        this.screenDate = cinemaMovie.getScreenDate();
        this.screenTime = cinemaMovie.getScreenTime();
        this.showTime = cinemaMovie.getShowTime();
    }

    public CinemaMovie toEntity(Movie movie, Cinema cinema){
        return CinemaMovie.builder()
                .movie(movie)
                .cinema(cinema)
                .screenDate(screenDate)
                .screenTime(screenTime).build();
    }
}
