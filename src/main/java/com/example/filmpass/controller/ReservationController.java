package com.example.filmpass.controller;

import com.example.filmpass.dto.ReservationDto;
import com.example.filmpass.entity.Reservation;
import com.example.filmpass.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
@Log4j2
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping()
    public ResponseEntity<ReservationDto> createReservation(@RequestBody ReservationDto reservationDto) {
        return ResponseEntity.ok(reservationService.create(reservationDto));
    }
}
