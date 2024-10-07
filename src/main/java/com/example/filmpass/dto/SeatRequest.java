package com.example.filmpass.dto;

import lombok.Data;

@Data
public class SeatRequest {
    private Long cinemaMovieId;
    private Long cinemaId;

    //좌석 하나하나
    private int rows;
    private int cols;
}
