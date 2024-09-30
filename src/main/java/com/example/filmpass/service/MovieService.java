package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.MovieInfoResponse;
import com.example.filmpass.dto.MovieResponseDTO;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MovieService {
    private final WebClient webClient;
    private final MovieRepository movieRepository;

    //일일박스오피스 API에서 1위부터 10위까지의 영화 불러오는 코드
    public List<DailyBoxOfficeDto> getDailyBoxOffice(String apiKey, String targetDate) {
        List<DailyBoxOfficeDto> dailyBoxOfficeDtos = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boxoffice/searchDailyBoxOfficeList.json") // API 경로
                        .queryParam("key", apiKey) // API 키를 쿼리 파라미터로 추가
                        .queryParam("targetDt", targetDate) // 날짜 추가
                        .build())
                .retrieve()
                .bodyToMono(MovieResponseDTO.class)
                .map(MovieResponseDTO::getBoxOfficeResult)
                .map(MovieResponseDTO.BoxOfficeResult::getDailyBoxOfficeList)
                .block();

        //1순위부터 10순위에 있는 영화 movieCd가져와서 상세 정보 API 불러오는 코드
            for (DailyBoxOfficeDto dailyBoxOfficeDto : dailyBoxOfficeDtos) {
                MovieInfoResponse movieDTO = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/movie/searchMovieInfo.json")
                                .queryParam("key", apiKey)
                                .queryParam("movieCd", dailyBoxOfficeDto.getMovieCd())
                                .build())
                        .retrieve()
                        .bodyToMono(MovieInfoResponse.class)
                        .block();
                log.info(movieDTO);
                Movie movie = movieDTO.toEntity();

                if (movieRepository.existsByMovieCd(movie.getMovieCd())) {
                    log.warn("Movie with movieCd {} already exists in DB", movie.getMovieCd());
                    continue;
                }
                movieRepository.save(movie);
            }
            return dailyBoxOfficeDtos;
       }


    public Movie getMovieInfo(String movieCd) {
         Movie movie = movieRepository.findByMovieCd(movieCd);
         return movie;
    }
}
