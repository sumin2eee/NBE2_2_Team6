package com.example.filmpass.service;

import com.example.filmpass.dto.ReservationDto;
import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Reservation;
import com.example.filmpass.entity.Seat;
import com.example.filmpass.repository.ReservationRepository;
import com.example.filmpass.repository.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    //예매 등록
    public ReservationDto create(ReservationDto reservationDto) {
        Seat seat = seatRepository.findById(reservationDto.getSeatId())
                .orElseThrow(() -> new EntityNotFoundException("해당 좌석이 없습니다"));

        if(!seat.isReserved()) {
            throw new IllegalStateException("예매한 좌석이 아닙니다");
        }

        Reservation reservation = reservationDto.toEntity(seat);
        reservationRepository.save(reservation);

        return new ReservationDto(reservation);
    }
}
