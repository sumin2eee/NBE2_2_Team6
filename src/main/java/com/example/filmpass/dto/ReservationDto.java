package com.example.filmpass.dto;

import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Member;
import com.example.filmpass.entity.Reservation;
import com.example.filmpass.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {
    private Long reservationId;
    private Long userId;
    private LocalDateTime reservationDate;
    private Long seatId;
    private Long cinemaMovieId;
    private int adult;
    private int child;
    private int youth;

    public ReservationDto(Reservation reservation) {
        this.reservationId = reservation.getReserveId();
        this.userId = reservation.getMember().getMemberId();
        this.reservationDate = reservation.getBookingDate();
        this.seatId = reservation.getSeat().getSeatId();
//        this.cinemaMovieId = reservation.getCinemaMovie().getCinemaMovieId();
        this.adult=reservation.getAdult();
        this.child=reservation.getChild();
        this.youth=reservation.getYouth();
    }

    public Reservation toEntity(Seat seat, CinemaMovie cinemaMovie, Member member) {

        return Reservation.builder()
                .reserveId(reservationId)
                .member(member)
                .seat(seat)
                .cinemaMovie(cinemaMovie)
                .bookingDate(LocalDateTime.now())
                .adult(adult)
                .youth(youth)
                .child(child).build();
    }
}
