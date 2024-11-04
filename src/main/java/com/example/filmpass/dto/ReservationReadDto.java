package com.example.filmpass.dto;

import com.example.filmpass.entity.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationReadDto {
    private Long userId;
    private LocalDateTime reservationDate;
    private Long seatId;
    private String cinemaMovieName;
    private Long reservationId;
    private int adult;
    private int child;
    private int youth;

    public ReservationReadDto(Reservation reservation) {
        this.userId = reservation.getMember().getMemberId();
        this.reservationDate = reservation.getBookingDate();
        this.seatId = reservation.getSeat().getSeatId();
        this.cinemaMovieName = reservation.getCinemaMovie().getMovie().getMovieName();
        this.reservationId = reservation.getReserveId();
        this.adult=reservation.getAdult();
        this.child=reservation.getChild();
        this.youth=reservation.getYouth();
    }
}
