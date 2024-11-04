package com.example.filmpass.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class MovieResponseDTO {
    private BoxOfficeResult boxOfficeResult;


    @Data
    public static class BoxOfficeResult {
        private String boxOfficeType;
        private String showRange;
        private List<DailyBoxOfficeDto> dailyBoxOfficeList;
    }

}
