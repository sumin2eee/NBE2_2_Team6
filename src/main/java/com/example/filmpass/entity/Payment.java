package com.example.filmpass.entity;

import com.example.filmpass.util.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.parameters.P;

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

    private Integer discount;

    private Integer amount;

    private String payToken;        //토스 결제하면 생성되는 토큰

    @Column(name = "paymentType")
    @Enumerated(EnumType.STRING)
    private PayType payType;

    private String paidTs;  //결제 일시

    private String orderNo; //나중에 예매이랑 연결되면 삭제 ?

    private Integer refundAmount;    // 환불된 금액 (부분 환불을 위해)

    @Enumerated(EnumType.STRING) // Enum을 String으로 저장
    private RefundStatus refundStatus;

    private LocalDateTime refundDate; // 환불 요청 날짜

//    @OneToOne //예매랑 연결되면 추가하기
//    @JoinColumn(name = "reservation_id")
//    private Reservation reservation;
}
