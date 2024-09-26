package com.example.filmpass.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
public class MovieResponseDTO {
    private BoxOfficeResult boxOfficeResult;
    @Data
    public static class BoxOfficeResult {
        private String boxOfficeType;
        private String showRange;
        private List<DailyBoxOfficeDto> dailyBoxOfficeList;
    }
    @Data
    public static class DailyBoxOfficeDto{
        private String rnum;
        private String rank;
        private String rankInten;
        private String rankOldAndNew;
        private String movieCd;
        private String movieNm;
        private String openDt;
        private String salesAmt;
        private String salesShare;
        private String salesInten;
        private String salesChange;
        private String salesAcc;
        private String audiCnt;
        private String audiInten;
        private String audiChange;
        private String audiAcc;
        private String scrnCnt;
        private String showCnt;
    }
}
