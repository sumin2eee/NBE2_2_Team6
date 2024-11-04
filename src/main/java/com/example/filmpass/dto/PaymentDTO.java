package com.example.filmpass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Log4j2
public class PaymentDTO {
    private Long ReserveId;     //예매번호
    private String apiKey;      //가맹점 Key
    private String productDesc;     //상품 설명
    private String retUrl;      //구매자 인증 완료 후 연결할 웹페이지
    private String retCancelUrl;    //결제를 중단할 때 이동시킬 취소 페이지
    private boolean autoExecute;     //자동 승인 여부(true = 바로 결제 / false = 판매자 승인 후 결제)
    private String resultCallback;  //성공 결과 전송할 URL
    private Integer amount;      //총 결제 금액
    private Integer amountTaxFree;   //결제 금액 중 비과세 금액
}
