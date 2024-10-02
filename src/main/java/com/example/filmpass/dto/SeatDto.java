package com.example.filmpass.dto;

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
    private int seatX;
    private int seatY;
    private boolean isReserved;

    public Seat toEntity() {
        Seat seat = new Seat();
        seat.setSeatRow(this.seatX);
        seat.setSeatCol(this.seatY);
        seat.setReserved(this.isReserved);
        return seat;
    }



}
