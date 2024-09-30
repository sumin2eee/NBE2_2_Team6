package com.example.filmpass.controller;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.MovieRepository;
import com.example.filmpass.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MovieController {
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final String apiKey = "236d4a6e256fa76f35804ceacdf28c39";

    //영화 목록 - 영화의 1순위부터 10순위까지 보여주는 코드
    @GetMapping("/dailyBoxOffice")
    public ResponseEntity<String> getAndUpdateDailyBoxOffice() {
        // 어제 날짜를 yyyyMMdd 형식으로 포맷팅
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String targetDate = yesterday.format(formatter);

        // MovieService를 호출하여 일일 박스오피스 정보를 가져옴
        List<DailyBoxOfficeDto> dailyBoxOfficeList = movieService.getDailyBoxOffice(apiKey, targetDate);

        // 영화 제목 리스트 생성
        List<String> movieTitles = dailyBoxOfficeList.stream()
                .map(DailyBoxOfficeDto::getMovieNm)  // 영화 제목만 리스트로 추출
                .collect(Collectors.toList());

        // 영화 포스터와 줄거리 업데이트 (영화 제목 리스트만 전달)
        movieService.updateMoviesWithPosterAndPlot(movieTitles);

        // 응답 반환
        return ResponseEntity.ok("Daily box office movies updated with poster and plot.");
    }

    @GetMapping("/dailyBoxOffice/{movieCd}")
    public Movie getMovieInfo(@PathVariable("movieCd") String movieCd) {
        return movieService.getMovieInfo(movieCd);
    }
}
