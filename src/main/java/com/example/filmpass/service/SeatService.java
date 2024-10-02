package com.example.filmpass.service;

import com.example.filmpass.dto.SeatDto;
import com.example.filmpass.dto.SeatRequest;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Seat;
import com.example.filmpass.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class SeatService {
    private final SeatRepository seatRepository;


    //좌석 생성
    public List<SeatDto> create(int rows, int cols) {
        List<SeatDto> seatDtoList = new ArrayList<>();

        //중복 생성 방지
        if (!seatRepository.findAll().isEmpty()) {
            log.info("Seats already created");
            return seatDtoList;
        }
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                SeatDto seatDto = new SeatDto();
                seatDto.setSeatId(seatRepository.findAll().get(0).getSeatId());
                seatDto.setSeatX(row);
                seatDto.setSeatY(col);
                seatDto.setReserved(false);

                Seat seat = seatDto.toEntity();
                seatRepository.save(seat);
                log.info("Creating seat: row={}, col={}", row, col);

                seatDtoList.add(seatDto);
            }
        }
        return seatDtoList;
    }

    //좌석 조회
    public List<SeatDto> read() {
        List<Seat> seats = seatRepository.findAll();
        List<SeatDto> seatDtoList = new ArrayList<>();

        for(Seat seat : seats) {
            SeatDto seatDto = new SeatDto();
            seatDto.setSeatId(seat.getSeatId());
            seatDto.setSeatX(seat.getSeatRow());
            seatDto.setSeatY(seat.getSeatCol());
            seatDto.setReserved(seat.isReserved());

            seatDtoList.add(seatDto);
        }
        return seatDtoList;
    }

    //좌석 선택
    public SeatDto reserveSeat(SeatRequest seatRequest) {
        Seat seat = seatRepository.findBySeatRowAndSeatCol(seatRequest.getRows(), seatRequest.getCols());
        if(!seat.isReserved()) {
            seat.setReserved(true);
            seatRepository.save(seat);
        }else {
            log.warn("Reserved Seat");
            throw new IllegalStateException("이미 예매된 좌석");

        }
        return SeatDto.builder()
                .seatId(seat.getSeatId())
                .seatX(seat.getSeatRow())
                .seatY(seat.getSeatCol())
                .isReserved(seat.isReserved())
                .build();
    }




}
