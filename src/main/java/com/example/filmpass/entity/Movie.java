package com.example.filmpass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Movie {    //영화 상세 정보 API에 있는 내용들과 비교해서 수정하기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    private String movieCd;

    private String movieName;

    private String movieNameEN;

    private String directorName;

    private String ageRating;    //관람등급

    private double movieRating;

    private String showTm; //러닝타임

    private String openDt;  //개봉년도

    @Lob  // 이 어노테이션을 추가하여 plot을 TEXT로 처리
    @Column(columnDefinition = "TEXT")  // 명시적으로 TEXT 타입 설정
    private String plot; //줄거리

    @Lob  // 이 어노테이션을 추가하여 plot을 TEXT로 처리
    @Column(columnDefinition = "TEXT")
    private String poster;
}