package com.example.filmpass.dto;

import com.example.filmpass.entity.CinemaMovie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieListDto {
    private String title;

    private List<CinemaMovieDto> info;

    public MovieListDto(Long id, CinemaMovie movieName,List<CinemaMovieDto> infoDto) {
        this.title = movieName.getMovie().getMovieName();
        this.info = infoDto;
    }


}
