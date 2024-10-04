package com.example.filmpass.controller;

import com.example.filmpass.dto.CinemaDto;
import com.example.filmpass.service.CinemaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cinema")
@Log4j2
public class CinemaController {
    private final CinemaService cinemaService;

//    {
//        "cinemaName": "5번 상영관",
//            "seatRow": 10,
//            "seatCol": 10
//
//    }
//  상영관 생성
    @PostMapping()
    public ResponseEntity<CinemaDto> createCinema(@RequestBody CinemaDto cinemaDto) {
        return ResponseEntity.ok(cinemaService.registerCinema(cinemaDto));
    }

    //상영관 조회
    @GetMapping()
    public ResponseEntity<List<CinemaDto>> read() {
        return ResponseEntity.ok(cinemaService.read());
    }
}
