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
    private Long paymentId; //

    private String status;

    private Integer discount;

    private Integer amount;

    private String payToken;        //토스 결제하면 생성되는 토큰

    private String orderNo;

    private Integer refundAmount;    // 환불된 금액 (부분 환불을 위해)

    @Enumerated(EnumType.STRING) // Enum을 String으로 저장
    private RefundStatus refundStatus;

    private LocalDateTime refundDate; // 환불 요청 날짜

//    @OneToOne
//    @JoinColumn(name = "reservation_id")
//    private Reservation reservation;
}
