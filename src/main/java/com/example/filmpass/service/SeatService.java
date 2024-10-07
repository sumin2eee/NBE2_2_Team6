package com.example.filmpass.service;

import com.example.filmpass.dto.CinemaMovieDto;
import com.example.filmpass.dto.SeatDto;
import com.example.filmpass.dto.SeatRequest;
import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Seat;
import com.example.filmpass.repository.CinemaMovieRepository;
import com.example.filmpass.repository.CinemaRepository;
import com.example.filmpass.repository.SeatRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final CinemaRepository cinemaRepository;
    private final CinemaMovieRepository cinemaMovieRepository;


    //좌석 생성
//    public List<SeatDto> create(SeatRequest seatRequest) {
//        List<SeatDto> seatDtoList = new ArrayList<>();
//
//        //중복 생성 방지
//        if (seatRepository.existsByCinemaCinemaId(seatRequest.getCinemaId()).orElse(false)) {
//            log.info("해당 상영관의 좌석이 이미 생성되었습니다");
//            return seatDtoList;
//        }
//
//        Cinema cinema = cinemaRepository.findById(seatRequest.getCinemaId()).orElseThrow(()-> new EntityNotFoundException("Cinema Not Found"));
//
//        int rows = cinema.getSeatRow();
//        int cols = cinema.getSeatCol();
//
//        for (int row = 1; row <= rows; row++) {
//            for (int col = 1; col <= cols; col++) {
//                SeatDto seatDto = new SeatDto();
//                seatDto.setSeatX(row);
//                seatDto.setSeatY(col);
//                seatDto.setReserved(false);
//                seatDto.setCinemaName(cinema.getCinemaName());
//
//                Seat seat = seatDto.toEntity(cinema);
//                seatRepository.save(seat);
//                log.info("Creating seat: row={}, col={}", row, col);
//
//                seatDto.setSeatId(seat.getSeatId());
//                seatDtoList.add(seatDto);
//            }
//        }
//        return seatDtoList;
//    }

    public List<SeatDto> create(SeatRequest seatRequest) {
        List<SeatDto> seatDtoList = new ArrayList<>();

        //중복 생성 방지
        if (seatRepository.existsByCinemaMovie_CinemaMovieIdAndCinema_cinemaId(seatRequest.getCinemaMovieId(), seatRequest.getCinemaId()).orElse(false)) {
            log.info("해당 상영정보의 좌석이 이미 생성되었습니다");
            return seatDtoList;
        }

        CinemaMovie cinemaMovie = cinemaMovieRepository.findById(seatRequest.getCinemaMovieId())
                .orElseThrow(() -> new EntityNotFoundException("CinemaMovie Not Found"));

        Cinema cinema = cinemaMovie.getCinema();

        int rows = cinema.getSeatRow();
        int cols = cinema.getSeatCol();

        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                SeatDto seatDto = new SeatDto();
                seatDto.setSeatX(row);
                seatDto.setSeatY(col);
                seatDto.setReserved(false);
                seatDto.setCinemaName(cinema.getCinemaName());
                seatDto.setCinemaMovieId(cinemaMovie.getCinemaMovieId());

                Seat seat = seatDto.toEntity(cinema, cinemaMovie);
                seatRepository.save(seat);
                log.info("Creating seat: row={}, col={}", row, col);

                seatDto.setSeatId(seat.getSeatId());
                seatDtoList.add(seatDto);
            }
        }
        return seatDtoList;
    }

    //좌석 조회
    public List<SeatDto> read(Long cinemaMovieId) {
        List<Seat> seats = seatRepository.findByCinemaMovieCinemaMovieId(cinemaMovieId);
        List<SeatDto> seatDtoList = new ArrayList<>();

        for(Seat seat : seats) {
            SeatDto seatDto = new SeatDto();
            seatDto.setSeatId(seat.getSeatId());
            seatDto.setSeatX(seat.getSeatRow());
            seatDto.setSeatY(seat.getSeatCol());
            seatDto.setCinemaName(seat.getCinema().getCinemaName());
            seatDto.setReserved(seat.isReserved());
            seatDto.setCinemaMovieId(seat.getCinemaMovie().getCinemaMovieId());

            seatDtoList.add(seatDto);
        }
        return seatDtoList;
    }

//    좌석 선택
    public SeatDto reserveSeat(SeatRequest seatRequest) {
        Seat seat = seatRepository.findBySeatRowAndSeatColAndCinemaMovieCinemaMovieId(seatRequest.getRows(), seatRequest.getCols(),seatRequest.getCinemaMovieId());
        if(!seat.isReserved()) {
            seat.setReserved(false);
//            seatRepository.save(seat);
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
