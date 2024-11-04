package com.example.filmpass.dto;
import com.example.filmpass.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
public class MovieInfoResponse {
    private MovieInfoResult movieInfoResult;
    private MovieInfoResponseKMDB kmdbInfo;

    @Data
    public static class MovieInfoResult {
        private MovieInfo movieInfo;
    }

    @Data
    public static class MovieInfo {
        private String movieCd;
        private String movieNm;
        private String movieNmEn;
        private String movieNmOg;
        private String showTm;
        private String openDt;
        private String prdtStatNm;
        private String typeNm;

        private List<Nation> nations;
        private List<Genre> genres;
        private List<Director> directors;
        private List<Actor> actors;
        private List<ShowType> showTypes;
        private List<Company> companys;
        private List<Audit> audits;
        private List<Staff> staffs;
    }

    @Data
    public static class Nation {
        private String nationNm;
    }

    @Data
    public static class Genre {
        private String genreNm;
    }

    @Data
    public static class Director {
        private String peopleNm;
        private String peopleNmEn;
    }

    @Data
    public static class Actor {
        private String peopleNm;
        private String peopleNmEn;
        private String cast;
        private String castEn;
    }

    @Data
    public static class ShowType {
        private String showTypeGroupNm;
        private String showTypeNm;
    }

    @Data
    public static class Company {
        private String companyCd;
        private String companyNm;
        private String companyNmEn;
        private String companyPartNm;
    }

    @Data
    public static class Audit {
        private String auditNo;
        private String watchGradeNm;
    }

    @Data
    public static class Staff {
        private String peopleNm;
        private String peopleNmEn;
        private String staffRoleNm;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)  // 알 수 없는 필드 무시
    public static class MovieInfoResponseKMDB {
        private String poster;
        private String plot;
    }

    public Movie toEntity() {
        String directorName = movieInfoResult.getMovieInfo().getDirectors() != null && !movieInfoResult.getMovieInfo().getDirectors().isEmpty()
                ? movieInfoResult.getMovieInfo().getDirectors().get(0).getPeopleNm()  // 첫 번째 감독 정보 가져오기
                : null;

        Movie movie = Movie.builder()
                .movieCd(movieInfoResult.getMovieInfo().getMovieCd())
                .movieName(movieInfoResult.getMovieInfo().getMovieNm())
                .movieNameEN(movieInfoResult.getMovieInfo().getMovieNmEn())
                .showTm(movieInfoResult.getMovieInfo().getShowTm())
                .openDt(movieInfoResult.getMovieInfo().getOpenDt())
                .poster(kmdbInfo != null ? kmdbInfo.getPoster() : null)
                .plot(kmdbInfo != null ? kmdbInfo.getPlot() : null)
                .ageRating(String.valueOf(movieInfoResult.getMovieInfo().audits.get(0).getWatchGradeNm()))
                .directorName(directorName)  // 감독 이름 설정
                .build();

        log.info("Created Movie entity: {}", movie);

        return movie;
    }


}