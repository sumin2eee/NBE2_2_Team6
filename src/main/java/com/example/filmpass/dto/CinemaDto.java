package com.example.filmpass.dto;

import com.example.filmpass.entity.Cinema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaDto {
    private Long id;
    private String cinemaName;
    //최대 좌석 행
    private int seatRow;

    //최대 좌석 열
    private int seatCol;

    public CinemaDto(Cinema cinema) {
        this.cinemaName = cinema.getCinemaName();
        this.seatRow = cinema.getSeatRow();
        this.seatCol = cinema.getSeatCol();
    }

    public Cinema toEntity(){
        Cinema cinema = Cinema.builder()
                .cinemaId(id)
                .cinemaName(cinemaName)
                .seatRow(seatRow)
                .seatCol(seatCol).build();
        return cinema;
    }


}
