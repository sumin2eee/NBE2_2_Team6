package com.example.filmpass.dto;

import com.example.filmpass.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KmdbMovieInfoResponse {
    @JsonProperty("title") // JSON 필드가 title일 경우
    private String title; // KMDB API 응답의 title 필드와 매칭

    @JsonProperty("plots") // JSON 필드가 plots일 경우
    private Plot[] plots; // 줄거리 배열

    @JsonProperty("posters") // JSON 필드가 posters일 경우
    private String posters; // 포스터 URL 목록

    // Plot 클래스 정의
    @Data
    public static class Plot {
        @JsonProperty("plotText")
        private String plotText; // 줄거리 텍스트
    }

    // DTO를 Movie 엔티티로 변환하는 메서드 추가
    public Movie toEntity(String movieCd, String movieName) {
        return Movie.builder()
                .movieCd(movieCd) // 영화 코드 (Kobis에서 가져온 movieCd)
                .movieName(movieName) // 영화 이름 (Kobis에서 가져온 영화 제목)
                .plot(getPlot()) // KMDB에서 가져온 줄거리
                .poster(getPosterUrl()) // KMDB에서 가져온 포스터 URL
                .build();
    }

    public String getPlot() {
        // 한국어 줄거리 반환 (최초의 plotText를 반환하도록 설정)
        return plots != null && plots.length > 0 ? plots[0].getPlotText() : null;
    }

    public String getPosterUrl() {
        // 첫 번째 포스터 URL 반환
        return posters != null && posters.contains("|") ? posters.split("\\|")[0] : posters;
    }
}
