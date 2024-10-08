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

    // 일일박스오피스 API에서 1위부터 10위까지의 영화 불러오는 코드
    public List<DailyBoxOfficeDto> getDailyBoxOffice(String apiKey, String targetDate) {
        log.info("Fetching daily box office data for target date: {}", targetDate);

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

        log.info("Fetched daily box office data: {}", dailyBoxOfficeDtos);

        // 1순위부터 10순위에 있는 영화 movieCd 가져와서 상세 정보 API 불러오는 코드
        for (DailyBoxOfficeDto dailyBoxOfficeDto : dailyBoxOfficeDtos) {
            log.info("Fetching movie details for movieCd: {}", dailyBoxOfficeDto.getMovieCd());

            MovieInfoResponse movieDTO = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/searchMovieInfo.json")
                            .queryParam("key", apiKey)
                            .queryParam("movieCd", dailyBoxOfficeDto.getMovieCd())
                            .build())
                    .retrieve()
                    .bodyToMono(MovieInfoResponse.class)
                    .block();

            log.info("Fetched movie details: {}", movieDTO);

            // 영화 엔티티로 변환
            Movie movie = movieDTO.toEntity();

            // 만약 영화가 이미 DB에 저장되어 있으면 스킵
            if (movieRepository.existsByMovieCd(movie.getMovieCd())) {
                log.warn("Movie with movieCd {} already exists in DB", movie.getMovieCd());
                continue;
            }

            // KOBIS에서 저장된 감독 이름으로 KMDB에서 포스터와 줄거리 가져오기
            String directorName = movie.getDirectorName(); // KOBIS에서 저장된 감독 이름

            // KMDB API 호출하여 포스터와 줄거리 업데이트
            updateMovieWithPosterAndPlot(movie, directorName);

            // 영화 정보 저장
            movieRepository.save(movie);
            log.info("Saved movie to DB: {}", movie);
        }

        return dailyBoxOfficeDtos;
    }

    //영화 제목으로 줄거리와 포스터 가져오는 코드
    public void updateMovieWithPosterAndPlot(Movie movie, String directorName) {
        log.info("Starting poster and plot update for movie: {}", movie.getMovieName());

        try {
            String kmdbApiKey = "P2428ABO640DWR015TI4";


            StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp");
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + kmdbApiKey);
            urlBuilder.append("&" + URLEncoder.encode("collection", "UTF-8") + "=kmdb_new2");
            urlBuilder.append("&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(movie.getMovieName(), "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("detail", "UTF-8") + "=Y");
            urlBuilder.append("&" + URLEncoder.encode("format", "UTF-8") + "=json");


            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");


            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }


            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();

            String movieDetails = sb.toString();
            log.info("KMDB API Response: {}", movieDetails);


            MovieInfoResponse.MovieInfoResponseKMDB movieInfo = parseMovieDetailsForDirector(movieDetails, directorName);

            if (movieInfo != null) {

                String firstPoster = movieInfo.getPoster() != null && movieInfo.getPoster().contains("|")
                        ? movieInfo.getPoster().split("\\|")[0]
                        : movieInfo.getPoster();

                movie.setPoster(firstPoster != null ? firstPoster : "");
                movie.setPlot(movieInfo.getPlot() != null ? movieInfo.getPlot() : "");
                log.info("Updated movie with poster: {}, plot: {}", movie.getPoster(), movie.getPlot());


                movieRepository.save(movie);
                log.info("Movie updated in DB: {}", movie.getMovieName());
            } else {
                log.warn("No movie details found in KMDB for: {}", movie.getMovieName());
            }
        } catch (Exception e) {
            log.error("Error while updating movie with poster and plot for: " + movie.getMovieName(), e);
        }
    }



    // JSON 파싱 메서드 (특정 감독으로 필터링하여 포스터와 줄거리만 추출)
    private MovieInfoResponse.MovieInfoResponseKMDB parseMovieDetailsForDirector(String movieDetails, String targetDirectorName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(movieDetails);

            JsonNode dataArray = root.path("Data");
            if (dataArray.isMissingNode() || !dataArray.isArray() || dataArray.size() == 0) {
                log.warn("No 'Data' array found in the response.");
                return null;
            }

            for (JsonNode movieNode : dataArray) {
                JsonNode resultArray = movieNode.path("Result");

                for (JsonNode resultNode : resultArray) {
                    JsonNode directorsNode = resultNode.path("directors").path("director");
                    if (!directorsNode.isMissingNode() && directorsNode.isArray() && directorsNode.size() > 0) {
                        for (JsonNode directorNode : directorsNode) {
                            String directorFromAPI = directorNode.path("directorNm").asText().trim();
                            log.info("Comparing director: '{}' with '{}'", directorFromAPI, targetDirectorName);

                            if (directorFromAPI.equalsIgnoreCase(targetDirectorName.trim())) {
                                log.info("Matching director found: {}", directorFromAPI);

                                String poster = resultNode.path("posters").asText("");
                                JsonNode plotsNode = resultNode.path("plots").path("plot");
                                String plot = "";
                                if (!plotsNode.isMissingNode() && plotsNode.isArray() && plotsNode.size() > 0) {
                                    plot = plotsNode.get(0).path("plotText").asText("");
                                }

                                MovieInfoResponse.MovieInfoResponseKMDB movieInfo = new MovieInfoResponse.MovieInfoResponseKMDB();
                                movieInfo.setPoster(poster);
                                movieInfo.setPlot(plot);
                                return movieInfo;
                            }
                        }
                    } else {
                        log.warn("No directors found in the result node.");
                    }
                }
            }

            log.warn("No matching director found in the results for: {}", targetDirectorName);
            return null;
        } catch (JsonProcessingException e) {
            log.error("Error while parsing movie details JSON", e);
            return null;
        }
    }



    public Movie getMovieInfo(String movieCd) {
         Movie movie = movieRepository.findByMovieCd(movieCd);
         return movie;
    }
}
