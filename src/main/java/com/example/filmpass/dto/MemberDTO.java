package com.example.filmpass.dto;

import com.example.filmpass.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberDTO {
    private String email;
    private String password;
    private String number;
    private String image;

    public Member toEntity(){
        return Member.builder()
                .email(email)
                .number(number)
                .image(image).build();
    }

}
