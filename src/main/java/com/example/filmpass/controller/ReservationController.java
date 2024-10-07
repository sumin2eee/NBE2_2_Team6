package com.example.filmpass.controller;

import com.example.filmpass.dto.ReservationDto;
import com.example.filmpass.dto.ReservationReadDto;
import com.example.filmpass.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
@Log4j2
public class ReservationController {
    private final ReservationService reservationService;


    //예매 등록
    @PostMapping()
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.create(reservationDto));
    }

    //예매 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReservationReadDto> read(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.read(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.remove(id);
        return ResponseEntity.noContent().build();
    }
}
