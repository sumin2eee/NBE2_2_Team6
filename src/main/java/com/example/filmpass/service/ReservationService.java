package com.example.filmpass.service;

import com.example.filmpass.dto.ReservationDto;
import com.example.filmpass.dto.ReservationReadDto;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Member;
import com.example.filmpass.entity.Reservation;
import com.example.filmpass.entity.Seat;
import com.example.filmpass.repository.CinemaMovieRepository;
import com.example.filmpass.repository.MemberRepository;
import com.example.filmpass.repository.ReservationRepository;
import com.example.filmpass.repository.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final CinemaMovieRepository cinemaMovieRepository;
    private final MemberRepository memberRepository;

    //예매 등록
    public ReservationDto create(ReservationDto reservationDto) {

        Seat seat = seatRepository.findById(reservationDto.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("해당 좌석이 없습니다"));

        CinemaMovie cinemaMovie = cinemaMovieRepository.findById(reservationDto.getCinemaMovieId())
                .orElseThrow(() -> new EntityNotFoundException("해당 영화가 없습니다"));

        Member member = memberRepository.findById(reservationDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 회원이 없습니다"));

        Optional<Reservation> error = reservationRepository.findBySeatSeatId(seat.getSeatId());

        if(error.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 좌석입니다");
        }

        if(!seat.isReserved()) {
            seat.setReserved(true);
            seatRepository.save(seat);
        }else {
            log.warn("Reserved Seat");
            throw new IllegalStateException("이미 예매된 좌석");

        }


        Reservation reservation = reservationDto.toEntity(seat,cinemaMovie,member);
        reservationRepository.save(reservation);

        return new ReservationDto(reservation);
    }

    //예매 조회
    public ReservationReadDto read(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        return new ReservationReadDto(reservation);
    }

    public void remove(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        Seat seat = seatRepository.findById(reservation.getSeat().getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("해당 좌석이 없습니다"));
        seat.setReserved(false);
        seatRepository.save(seat);

        reservationRepository.delete(reservation);
    }
}
