package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.KmdbMovieInfoResponse;
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
    private final WebClient kmdbWebClient;

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

            // KMDB API를 통해 줄거리와 포스터 URL 가져오기
            KmdbMovieInfoResponse kmdbResponse = kmdbWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search_json2.jsp")
                            .queryParam("collection", "kmdb_new2")
                            .queryParam("title", movie.getMovieName()) // Kobis에서 가져온 영화 제목으로 검색
                            .queryParam("ServiceKey", "P2428ABO640DWR015TI4") // 인증키
                            .build())
                    .retrieve()
                    .bodyToMono(KmdbMovieInfoResponse.class)
                    .block();

// KMDB 응답을 Movie 엔티티로 변환하고 저장
            if (kmdbResponse != null) { // null 체크 추가
                // KMDB 응답을 Movie 엔티티로 변환
                Movie kmdbMovie = kmdbResponse.toEntity(movie.getMovieCd(), movie.getMovieName());

                // KMDB API로부터 받은 영화 정보를 로그에 출력
                log.info("Saved KMDB Movie Info: Title: {}, Plot: {}, Poster URL: {}",
                        kmdbResponse.getTitle(),
                        kmdbResponse.getPlot(),
                        kmdbResponse.getPosterUrl());
            } else {
                log.warn("No KMDB response for movie: {}", movie.getMovieName());
            }


            movieRepository.save(movie); //영화 정보 저장
        }
        return dailyBoxOfficeDtos;
    }

    public Movie getMovieInfo(String movieCd) {
        Movie movie = movieRepository.findByMovieCd(movieCd);
        return movie;
    }

}




/*
package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.KmdbMovieInfoResponse;
import com.example.filmpass.dto.MovieInfoResponse;
import com.example.filmpass.dto.MovieResponseDTO;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.MovieRepository;
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson ObjectMapper 임포트
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MovieService {
    private final WebClient webClient;
    private final MovieRepository movieRepository;
    private final WebClient kmdbWebClient;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper


    // 일일박스오피스 API에서 1위부터 10위까지의 영화 불러오는 코드
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

        // 1순위부터 10순위에 있는 영화 movieCd 가져와서 상세 정보 API 불러오는 코드
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
                log.info("Movie with movieCd {} already exists in DB", movie.getMovieCd());
                continue; // DB에 존재하는 경우 다음 영화로 넘어감
            }

            // KMDB API를 통해 줄거리와 포스터 URL 가져오기
            String kmdbResponseString = kmdbWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search_json2.jsp")
                            .queryParam("collection", "kmdb_new2")
                            .queryParam("title", movie.getMovieName()) // Kobis에서 가져온 영화 제목으로 검색
                            .queryParam("ServiceKey","P2428ABO640DWR015TI4") // 외부화된 인증키
                            .build())
                    .retrieve()
                    .bodyToMono(String.class) // 응답을 String으로 받기
                    .block();

            // String을 JSON으로 변환
            KmdbMovieInfoResponse kmdbResponse = null;
            if (kmdbResponseString != null) {
                try {
                    kmdbResponse = objectMapper.readValue(kmdbResponseString, KmdbMovieInfoResponse.class);
                } catch (Exception e) {
                    log.error("Failed to parse KMDB response for movie: {}", movie.getMovieName(), e);
                }
            }

            // KMDB 응답을 Movie 엔티티로 변환하고 저장
            if (kmdbResponse != null) { // null 체크 추가
                // KMDB 응답을 Movie 엔티티로 변환
                Movie kmdbMovie = kmdbResponse.toEntity(movie.getMovieCd(), movie.getMovieName());

                // KMDB 영화 정보를 로그에 출력
                log.info("Saved KMDB Movie Info: Title: {}, Plot: {}, Poster URL: {}",
                        kmdbMovie.getMovieName(),
                        kmdbMovie.getPlot(),
                        kmdbMovie.getPoster());

                // KMDB 영화 정보 저장
                movieRepository.save(kmdbMovie); // KMDB 영화 정보 저장
            } else {
                log.warn("No KMDB response for movie: {}", movie.getMovieName());
            }

            // 영화 정보 저장 (이미 저장된 경우가 아니라면)
            movieRepository.save(movie); // 영화 정보 저장
        }
        return dailyBoxOfficeDtos;
    }

    public Movie getMovieInfo(String movieCd) {
        return movieRepository.findByMovieCd(movieCd);
    }
}
*/