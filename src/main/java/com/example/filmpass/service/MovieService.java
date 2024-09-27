package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.MovieResponseDTO;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {
    private final WebClient webClient;
    private final MovieRepository movieRepository;

    public Mono<Void> dailyBoxOffice(String apiKey, String targetDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boxoffice/searchDailyBoxOfficeList.json")
                        .queryParam("key", apiKey)
                        .queryParam("targetDt", targetDate)
                        .build())
                .retrieve()
                .bodyToMono(MovieResponseDTO.class)
                .map(MovieResponseDTO::getBoxOfficeResult)
                .map(MovieResponseDTO.BoxOfficeResult::getDailyBoxOfficeList)
                .map(this::toEntity)
                .doOnNext(movies -> movieRepository.saveAll(movies))
                .then();
    }

    private List<Movie> toEntity(List<DailyBoxOfficeDto> dailyBoxOfficeDtoList) {
        return dailyBoxOfficeDtoList.stream()
                .map(dto -> Movie.builder()
                        .movieId(dto.getId())
                        .title(dto.getMovieNm())
                        .build())
                .collect(Collectors.toList());
    }
}