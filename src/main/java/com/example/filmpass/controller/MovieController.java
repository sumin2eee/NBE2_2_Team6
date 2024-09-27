package com.example.filmpass.controller;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.repository.MovieRepository;
import com.example.filmpass.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MovieController {
    private final MovieService movieService;
    private final MovieRepository movieRepository;
    private final String apiKey = "236d4a6e256fa76f35804ceacdf28c39";

    //영화 목록 - 영화의 1순위부터 10순위까지 보여주는 코드
    @GetMapping("/dailyBoxOffice")
    public List<DailyBoxOfficeDto> getDailyBoxOffice() {
        // 어제 날짜를 yyyyMMdd 형식으로 포맷팅
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String targetDate = yesterday.format(formatter);
        // MovieService를 호출하여 일일 박스오피스 정보를 가져옵니다.
        return movieService.getDailyBoxOffice(apiKey, targetDate);
    }





//@GetMapping("/dailyBoxOffice/{movieCd}")
//public Mono<List<Movie>> getDailyBoxOffice() {
//    // 오늘 날짜를 yyyyMMdd 형식으로 포맷팅
//    String targetDate = "20120101";
//    // MovieService를 호출하여 일일 박스오피스 정보를 가져옵니다.
//    return movieService.getDailyBoxOffice(apiKey, targetDate)
//            .flatMapMany(Flux::fromIterable)
//            .map(dailyBoxOfficeDto -> {
//                Movie movie = new Movie();
//                movie.setTitle((dailyBoxOfficeDto.getMovieNm())); //예시로 넣어본 거라 나중에 상세 정보까지 가져와서 각각 맞게 바꿔야할 것 같아요
//
//                return movieRepository.save(movie);
//            }).collectList();
//}
////여기까지는 하면 entity에 저장 -- 이걸 서비스나 레포지토리로 옮겨야 할 것 같아요 순위 가져오는 동시에 상세 정보가 DB에 저장 되도록


}