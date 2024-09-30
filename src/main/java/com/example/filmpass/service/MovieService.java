package com.example.filmpass.service;

import com.example.filmpass.dto.DailyBoxOfficeDto;
import com.example.filmpass.dto.MovieInfoResponse;
import com.example.filmpass.dto.MovieResponseDTO;
import com.example.filmpass.entity.Movie;
import com.example.filmpass.repository.MovieRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class MovieService {
    private final WebClient webClient;
    private final MovieRepository movieRepository;
    private final WebClient webClientKMDB;

    //일일박스오피스 API에서 1위부터 10위까지의 영화 불러오는 코드
    public List<DailyBoxOfficeDto> getDailyBoxOffice(String apiKey, String targetDate) {
        List<DailyBoxOfficeDto> dailyBoxOfficeDtos = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boxoffice/searchDailyBoxOfficeList.json") // API 경로
                        .queryParam("key", apiKey) // API 키를 쿼리 파라미터로 추가
                        .queryParam("targetDt", targetDate) // 날짜 추가
                        .build())
                .retrieve()
                .bodyToMono(MovieResponseDTO.class)
                .map(MovieResponseDTO::getBoxOfficeResult)
                .map(MovieResponseDTO.BoxOfficeResult::getDailyBoxOfficeList)
                .block();

        //1순위부터 10순위에 있는 영화 movieCd가져와서 상세 정보 API 불러오는 코드
            for (DailyBoxOfficeDto dailyBoxOfficeDto : dailyBoxOfficeDtos) {
                MovieInfoResponse movieDTO = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/movie/searchMovieInfo.json")
                                .queryParam("key", apiKey)
                                .queryParam("movieCd", dailyBoxOfficeDto.getMovieCd())
                                .build())
                        .retrieve()
                        .bodyToMono(MovieInfoResponse.class)
                        .block();
                log.info(movieDTO);
                Movie movie = movieDTO.toEntity();

                if (movieRepository.existsByMovieCd(movie.getMovieCd())) {
                    log.warn("Movie with movieCd {} already exists in DB", movie.getMovieCd());
                    continue;
                }
                movieRepository.save(movie);
            }
            return dailyBoxOfficeDtos;
       }


    public Movie getMovieInfo(String movieCd) {
         Movie movie = movieRepository.findByMovieCd(movieCd);
         return movie;
    }


    public void updateMoviesWithPosterAndPlot(List<String> movieTitles) {
        String kmdbApiKey = "P2428ABO640DWR015TI4"; // KMDB API 키

        for (String movieTitle : movieTitles) {
            try {
                // KMDB API URL 빌드
                StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp");
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + kmdbApiKey);
                urlBuilder.append("&" + URLEncoder.encode("collection", "UTF-8") + "=kmdb_new2");
                urlBuilder.append("&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(movieTitle, "UTF-8"));
                urlBuilder.append("&" + URLEncoder.encode("detail", "UTF-8") + "=Y");
                urlBuilder.append("&" + URLEncoder.encode("format", "UTF-8") + "=json");

                // URL 객체 생성 및 연결 설정
                URL url = new URL(urlBuilder.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");

                // 응답 코드 확인 및 데이터 읽기
                BufferedReader rd;
                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                // 응답 데이터 읽기
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
                conn.disconnect();

                String movieDetails = sb.toString();
                log.info("KMDB API Response for movie '{}': {}", movieTitle, movieDetails);

                // JSON 파싱 후 DB에 저장하는 로직 추가
                MovieInfoResponse.MovieInfoResponseKMDB movieInfo = parseMovieDetails(movieDetails);

                if (movieInfo != null) {
                    Optional<Movie> optionalMovie = movieRepository.findByMovieName(movieTitle);

                    if (optionalMovie.isPresent()) {
                        Movie movie = optionalMovie.get();
                        movie.setPoster(movieInfo.getPoster());
                        movie.setPlot(movieInfo.getPlot());
                        movieRepository.save(movie); // 업데이트
                        log.info("Updated movie with poster and plot: " + movieTitle);
                    } else {
                        log.warn("Movie not found in DB: " + movieTitle);
                    }
                } else {
                    log.warn("No movie details found in KMDB for: " + movieTitle);
                }
            } catch (Exception e) {
                log.error("Error while updating movie with poster and plot for: " + movieTitle, e);
            }
        }
    }


    // JSON 파싱 메서드 (KMDB 응답에서 포스터와 줄거리만 추출)
    private MovieInfoResponse.MovieInfoResponseKMDB parseMovieDetails(String movieDetails) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(movieDetails);

            // 포스터와 줄거리 추출
            JsonNode resultNode = root.path("Data").get(0).path("Result").get(0);
            String poster = resultNode.path("posters").asText();
            String plot = resultNode.path("plots").path("plot").get(0).path("plotText").asText();

            MovieInfoResponse.MovieInfoResponseKMDB movieInfo = new MovieInfoResponse.MovieInfoResponseKMDB();
            movieInfo.setPoster(poster);
            movieInfo.setPlot(plot);

            return movieInfo;
        } catch (JsonProcessingException e) {
            log.error("Error while parsing movie details JSON", e);
            return null;
        }
    }


}





