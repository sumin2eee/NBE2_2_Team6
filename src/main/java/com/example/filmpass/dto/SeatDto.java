package com.example.filmpass.dto;

import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatDto {
    private Long seatId;
    private String cinemaName;
    private int seatX;
    private int seatY;
    private boolean isReserved;
    private Long cinemaMovieId;
//    private Long cinemaId;

    public Seat toEntity(Cinema cinema, CinemaMovie cinemaMovie) {
        Seat seat = new Seat();
        seat.setSeatRow(this.seatX);
        seat.setSeatCol(this.seatY);
        seat.setReserved(this.isReserved);
        seat.setCinema(cinema);
        seat.setCinemaMovie(cinemaMovie);
        return seat;
    }



}
