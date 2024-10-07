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
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class CinemaMovieService {
    private final CinemaMovieRepository cinemaMovieRepository;
    private final CinemaRepository cinemaRepository;
    private final MovieRepository movieRepository;

    public List<CinemaMovieDto> registerCinema() {
        List<Movie> movies = movieRepository.findAll();
        List<CinemaMovieDto> moviesDto = new ArrayList<>();

        Random random = new Random();

        for (Movie movie : movies) {
            int allminutes = Integer.parseInt(movie.getShowTm());
            int hour = allminutes / 60;
            int min = allminutes % 60;
            LocalTime showtime = LocalTime.of(hour, min);
            if(cinemaMovieRepository.findByMovie_MovieId(movie.getMovieId()).isEmpty()){
                for (int i = 0; i < 7; i++) {
                    long cinemaId = random.nextInt(5)+1;

                    Cinema cinema = cinemaRepository.findById(cinemaId).get();
                    CinemaMovie cinemaMovie = CinemaMovie.builder()
                            .movie(movie) // movie 객체 설정
                            .screenDate(LocalDate.from(LocalDateTime.now().plusDays(i))) // 오늘부터 i일 더한 날짜로 설정
                            .screenTime(showtime) // showtime은 상영 시간으로 설정
                            .showTime(generateRandomTime())
                            .cinema(cinema)
                            .build();

                    cinemaMovieRepository.save(cinemaMovie); // cinemaMovie 저장
                    CinemaMovieDto cinemaMovieDto = new CinemaMovieDto(
                            cinemaMovie.getCinemaMovieId(),
                            cinemaMovie.getMovie(),
                            cinemaMovie.getScreenDate(),
                            cinemaMovie.getScreenTime(),
                            cinemaMovie.getShowTime(),
                            cinemaMovie.getMovie().getMovieName(),
                            cinemaMovie.getCinema());

                    moviesDto.add(cinemaMovieDto);
                }
            }
        }
        return moviesDto;
    }

    private LocalTime generateRandomTime() {
        Random random = new Random();
        int hour = random.nextInt(17)+7;
        int min = random.nextInt(60);
        return LocalTime.of(hour, min);
    }


//    public CinemaMovieDto registerCinema(CinemaMovieDto cinemaMovieDto) {
//        Movie movie = movieRepository.findById(cinemaMovieDto.getMovieId()).get();
//        Cinema cinema = cinemaRepository.findById(cinemaMovieDto.getCinemaId()).get();
//
//        Optional<CinemaMovie> error = cinemaMovieRepository
//                .findByMovieMovieIdAndCinemaCinemaIdAndScreenDateAndScreenTime(movie.getMovieId(), cinema.getCinemaId(), cinemaMovieDto.getScreenDate(), cinemaMovieDto.getScreenTime());
//
//        if (error.isPresent()) {
//            throw new IllegalArgumentException("이미 등록된 영화의 상영정보 입니다");
//        }
//
//        CinemaMovie cinemaMovie = cinemaMovieDto.toEntity(movie, cinema);
//        CinemaMovie savedCinemaMovie = cinemaMovieRepository.save(cinemaMovie);
//        return new CinemaMovieDto(movie.getMovieId(), savedCinemaMovie);
//    }

    //        상영중인 영화 상영정보 조회
    public MovieListDto read(Long movieId) {
        List<CinemaMovie> cinemaMovieList = cinemaMovieRepository.findByMovie_MovieId(movieId);

        List<CinemaMovieDto> infoDto = new ArrayList<>();
        for (CinemaMovie cinemaMovie : cinemaMovieList) {
            infoDto.add(new CinemaMovieDto(movieId, cinemaMovie));
        }

        if (cinemaMovieList.isEmpty()) {
            throw new IllegalStateException("상영정보가 없습니다");
        }
        CinemaMovie movieName = cinemaMovieList.get(0);

        return new MovieListDto(movieId, movieName, infoDto);
    }
}