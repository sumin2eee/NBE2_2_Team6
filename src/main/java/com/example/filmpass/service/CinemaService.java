package com.example.filmpass.service;

import com.example.filmpass.dto.CinemaDto;
import com.example.filmpass.dto.SeatDto;
import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.Seat;
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
public class CinemaService {
    private final CinemaRepository cinemaRepository;
    private final SeatRepository seatRepository;

    //상영관 등록
    public CinemaDto registerCinema(CinemaDto cinemaDto) {
        Cinema cinema = cinemaDto.toEntity();
        cinemaRepository.save(cinema);
        return new CinemaDto(cinema);
    }

    //상영관 조회
    public List<CinemaDto> read() {
        List<Cinema> cinemas = cinemaRepository.findAll();
        List<CinemaDto> cinemaDtoList = new ArrayList<>();

        for(Cinema cinema : cinemas) {
            CinemaDto cinemaDto = new CinemaDto();
            cinemaDto.setId(cinema.getCinemaId());
            cinemaDto.setCinemaName(cinema.getCinemaName());
            cinemaDto.setSeatRow(cinema.getSeatRow());
            cinemaDto.setSeatCol(cinema.getSeatCol());

            cinemaDtoList.add(cinemaDto);
        }
        return cinemaDtoList;
    }
}
