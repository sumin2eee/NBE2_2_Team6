package com.example.filmpass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.parameters.P;

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
    
//    @OneToOne //예매랑 연결되면 추가하기
//    @JoinColumn(name = "reservation_id")
//    private Reservation reservation;
}
