package com.example.filmpass.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Size(min = 8)
    @Column(unique = true)
    private String password;

    @Column(unique = true)
    private String email;

    @NotNull
    @Size(min = 10, max = 15)
    @Column(unique = true)
    private String number; // 폰번호

    private String image; //프로필 사진
    private String role; // 권한


    /*@JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Reservation> orders = new ArrayList<>();*/

}