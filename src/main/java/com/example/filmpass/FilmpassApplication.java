package com.example.filmpass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FilmpassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmpassApplication.class, args);
    }

}