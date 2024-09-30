package com.example.filmpass.dto;
import com.example.filmpass.entity.Movie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

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
        private String prdtYear;
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
        return Movie.builder()
                .movieCd(movieInfoResult.getMovieInfo().movieCd)
                .movieName(movieInfoResult.getMovieInfo().movieNm)
                .showTm(movieInfoResult.getMovieInfo().showTm)
                .movieNameEN(movieInfoResult.getMovieInfo().movieNmEn)
                .openDt(movieInfoResult.getMovieInfo().openDt)
                .poster(kmdbInfo != null ? kmdbInfo.getPoster() : null)
                .plot(kmdbInfo != null ? kmdbInfo.getPlot() : null)
                .build();
    }
}