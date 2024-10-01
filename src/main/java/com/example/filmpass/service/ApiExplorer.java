package com.example.filmpass.service;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

public class ApiExplorer {
    public static void main(String[] args) throws IOException {
        // KMDB API 요청 URL 빌드
        StringBuilder urlBuilder = new StringBuilder("http://api.koreafilm.or.kr/openapi-data2/wisenut/search_api/search_json2.jsp");

        // 파라미터 설정 (영화 제목: "더 커버넌트", 감독: "가이 리치")
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=P2428ABO640DWR015TI4");  // KMDB API Key
        urlBuilder.append("&" + URLEncoder.encode("collection", "UTF-8") + "=kmdb_new2");  // 고정값
        urlBuilder.append("&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode("더 커버넌트", "UTF-8"));  // 영화 제목 인코딩
        urlBuilder.append("&" + URLEncoder.encode("director", "UTF-8") + "=" + URLEncoder.encode("가이 리치", "UTF-8"));  // 감독명 인코딩
        urlBuilder.append("&" + URLEncoder.encode("detail", "UTF-8") + "=Y");  // 상세 정보 요청
        urlBuilder.append("&" + URLEncoder.encode("format", "UTF-8") + "=json");  // JSON 형식으로 응답

        // URL 객체 생성 및 연결
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        // 응답 코드 확인
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

        // 응답 출력
        System.out.println("KMDB API Response for '더 커버넌트' with director '가이 리치':");
        System.out.println(sb.toString());

        // 필요한 필드 (포스터, 줄거리 등) 파싱 로직 추가 가능
    }
}
