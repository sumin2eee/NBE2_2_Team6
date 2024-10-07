package com.example.filmpass.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    private String status;

    private Integer discount;

    private Integer amount;

    private String payToken;        //토스 결제하면 생성되는 토큰

//    @OneToOne
//    @JoinColumn(name = "reservation_id")
//    private Reservation reservation;
}
