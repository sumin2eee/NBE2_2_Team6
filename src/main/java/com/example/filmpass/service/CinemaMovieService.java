package com.example.filmpass.service;

import com.example.filmpass.dto.CinemaMovieDto;
import com.example.filmpass.dto.MovieListDto;
import com.example.filmpass.entity.Cinema;
import com.example.filmpass.entity.CinemaMovie;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.CinemaMovieRepository;
import com.example.filmpass.repository.CinemaRepository;
import com.example.filmpass.repository.MovieRepository;
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
public class CinemaMovieService {
    private final CinemaMovieRepository cinemaMovieRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;

    public CinemaMovieDto registerCinema(CinemaMovieDto cinemaMovieDto) {
        Movie movie = movieRepository.findById(cinemaMovieDto.getMovieId()).get();
        Cinema cinema = cinemaRepository.findById(cinemaMovieDto.getCinemaId()).get();

        Optional<CinemaMovie> error = cinemaMovieRepository
                .findByMovieMovieIdAndCinemaCinemaIdAndScreenDateAndScreenTime(movie.getMovieId(), cinema.getCinemaId(), cinemaMovieDto.getScreenDate(), cinemaMovieDto.getScreenTime());

        if(error.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 영화의 상영정보 입니다");
        }

        CinemaMovie cinemaMovie = cinemaMovieDto.toEntity(movie, cinema);
        CinemaMovie savedCinemaMovie = cinemaMovieRepository.save(cinemaMovie);
        return new CinemaMovieDto(movie.getMovieId(), savedCinemaMovie);
    }

    //상영중인 영화 상영정보 조회
    public MovieListDto read(Long movieId) {
        List<CinemaMovie> cinemaMovieList = cinemaMovieRepository.findByMovie_MovieId(movieId);

        List<CinemaMovieDto> infoDto = new ArrayList<>();
        for(CinemaMovie cinemaMovie : cinemaMovieList) {
            infoDto.add(new CinemaMovieDto(movieId, cinemaMovie));
        }

        CinemaMovie movieName = cinemaMovieList.get(0);

        return new MovieListDto(movieId, movieName, infoDto);
    }


}
