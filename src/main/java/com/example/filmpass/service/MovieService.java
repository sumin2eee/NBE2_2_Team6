package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.MovieResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MovieService {
    private final WebClient webClient;

    public Mono<List<DailyBoxOfficeDto>> getDailyBoxOffice(String apiKey, String targetDate) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boxoffice/searchDailyBoxOfficeList.json") // API 경로
                        .queryParam("key", apiKey) // API 키를 쿼리 파라미터로 추가
                        .queryParam("targetDt", targetDate) // 날짜 추가
                        .build())
                .retrieve()
                .bodyToMono(MovieResponseDTO.class)
                .map(MovieResponseDTO::getBoxOfficeResult)
                .map(MovieResponseDTO.BoxOfficeResult::getDailyBoxOfficeList);

    }


}
