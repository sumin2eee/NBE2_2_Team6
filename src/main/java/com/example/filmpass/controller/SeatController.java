package com.example.filmpass.controller;

import com.example.filmpass.dto.SeatDto;
import com.example.filmpass.dto.SeatRequest;
import com.example.filmpass.entity.Seat;
import com.example.filmpass.repository.SeatRepository;
import com.example.filmpass.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seat")
@Log4j2
public class SeatController {
    private final SeatService seatService;
    private final SeatRepository seatRepository;

    //좌석 생성
//    {
//        "cinemaId": 1
//    }
    @PostMapping()
    public ResponseEntity<List<SeatDto>> create(@RequestBody SeatRequest seatRequest) {
        List<SeatDto> createdSeats = seatService.create(seatRequest);
        return ResponseEntity.ok(createdSeats);
    }

    //좌석 조회
    @GetMapping("/{cinemaMovieId}")
    public List<SeatDto> read(@PathVariable Long cinemaMovieId) {
        return seatService.read(cinemaMovieId);
    }

    //좌석 선택
//    {
//        "cinemaMovieId":2,
//            "rows":3,
//            "cols":7
//    }
    @PutMapping("/choice")
    public ResponseEntity<SeatDto> update(@RequestBody SeatRequest seatRequest) {
        return ResponseEntity.ok(seatService.reserveSeat(seatRequest));
    }

//    @PutMapping("/cancel")
//    public ResponseEntity<Seat> cancel(@RequestBody SeatRequest seatRequest) {
//        Seat seat = seatRepository.findById(seatRequest.getCinemaMovieId()).orElseThrow();
//        seat.setReserved(false);
//        return ResponseEntity.ok(seatRepository.save(seat));
//    }
}
