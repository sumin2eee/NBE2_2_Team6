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
//        "rows": 5,
//            "cols": 10
//    }
    @PostMapping()
    public ResponseEntity<List<SeatDto>> create(@RequestBody SeatRequest seatRequest) {
        List<SeatDto> createdSeats = seatService.create(seatRequest.getRows(), seatRequest.getCols());
        return ResponseEntity.ok(createdSeats);
    }

    //좌석 조회
    @GetMapping()
    public List<SeatDto> read() {
        return seatService.read();
    }

    //좌석 예매
    @PutMapping("/choice")
    public ResponseEntity<SeatDto> update(@RequestBody SeatRequest seatRequest) {
        return ResponseEntity.ok(seatService.reserveSeat(seatRequest));
    }
}
