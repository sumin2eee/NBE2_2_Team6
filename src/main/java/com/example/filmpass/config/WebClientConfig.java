package com.example.filmpass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // 기존 Kobis API용 WebClient
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://www.kobis.or.kr/kobisopenapi/webservice/rest")
                .build();
    }

    // KMDB API용 WebClient 추가
    @Bean
    public WebClient kmdbWebClient() {
        return WebClient.builder()
                .baseUrl("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api") // 경로 수정
                .defaultHeader("Content-Type", "application/json") // 필요한 경우 헤더 설정
                .build();
    }
}
