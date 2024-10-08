package com.example.filmpass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundDTO {
    private Long reserveId; // 주문 번호
    private Integer amount; // 환불할 금액
    private LocalDateTime refundRequestDate; // 환불 요청 날짜
}
