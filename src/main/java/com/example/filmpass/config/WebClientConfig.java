package com.example.filmpass.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://www.kobis.or.kr/kobisopenapi/webservice/rest")
                .build();
    }
    @Bean
    public WebClient webClientKMDB() {
        return WebClient.builder()
                .baseUrl("http://api.koreafilm.or.kr")
                .build();
    }
}
