package com.example.filmpass.util;

public enum RefundStatus {
    PENDING,  // 환불 대기 중, 지연 중? delay? 일단 pending으로 했습니다.
    REFUNDED, // 환불 완료
    FAILED    // 환불 실패
}

