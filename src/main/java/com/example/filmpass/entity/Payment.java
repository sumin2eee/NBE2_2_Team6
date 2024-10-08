package com.example.filmpass.entity;

import com.example.filmpass.util.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long PaymentId;

    @Column(name = "paymentStatus")
    @Enumerated(EnumType.STRING)
    private PayStatus status;

    private Integer amount;

    private String payToken;        //토스 결제하면 생성되는 토큰

    @Column(name = "paymentType")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    private LocalDateTime paidTs;  //결제 일시

    private Integer refundAmount = 0;    // 환불된 금액 (부분 환불을 위해)

    private Integer availableRefundAmount; // 결제금액 - 누적된 횐불 금액 값이 들어감

    @Enumerated(EnumType.STRING) // Enum을 String으로 저장
    private RefundStatus refundStatus;

    private LocalDateTime refundRequestDate; // 환불 요청 날짜

    private LocalDateTime refundDate; // 환불 처리 날짜



    private LocalDateTime createdTs;

    @OneToOne //예매랑 연결되면 추가하기
    @JoinColumn(name = "reserve_id")
    private Reservation reservation;
}
