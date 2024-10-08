//package com.example.filmpass.util;
//
//import com.example.filmpass.service.MovieService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//
//@Service
//@RequiredArgsConstructor
//public class Scheculer {
//    private final MovieService movieService;
//
//    @Scheduled(cron = "0 * * * * *")
//    public void fetchAndSaveDailyBoxOffice() {
//        String apikey = "236d4a6e256fa76f35804ceacdf28c39";
//        LocalDateTime today = LocalDateTime.now();
//        LocalDateTime yesterday = today.minusDays(1);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        String targetDate = yesterday.format(formatter);
//
//        movieService.dailyBoxOffice(apikey, targetDate)
//                .subscribe(
//                        null,
//                        error -> System.err.println("Failed to fetch and save data: " + error.getMessage())
//                );
//    }
//
//}
