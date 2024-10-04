package com.example.filmpass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieListDto {
    private Long movieId;

    private List<CinemaMovieDto> info;
}
