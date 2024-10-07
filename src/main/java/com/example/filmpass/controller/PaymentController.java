package com.example.filmpass.controller;

import com.example.filmpass.dto.PaymentDTO;
import com.example.filmpass.dto.RefundDTO;
import com.example.filmpass.entity.Payment;
import com.example.filmpass.repository.PaymentRepository;
import com.example.filmpass.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@AllArgsConstructor
@RestController
@Log4j2
@RequestMapping("/pay")
public class PaymentController {
    private final PaymentService paymentService;

    //결제 페이지
    //test JSON 예시
    // {
    //  "orderNo": 12225,
    //	"productDesc": "테스 상품",
    //  "retUrl": "http://localhost:8080/pay/return",
    //  "retCancelUrl": "http://localhost:8080/pay/cancel",
    //  "autoExecute": true,
    //  "resultCallback": "",
    //  "amount": 200,
    //  "amountTaxFree": 0
    //  }
    @PostMapping("/payments")
    public ResponseEntity<String> payment(@RequestBody PaymentDTO paymentDTO){
        return paymentService.payment(paymentDTO);

}
    //결제완료 후 돌아갈 페이지
    @GetMapping("/return")
    public String returnPage(){
        return "결제 완료";   //나중에 예매 정보하고 추가
    }

    @GetMapping("/cancel")
    public String cancelPage() {
        return "결제 취소";
    }

    @PostMapping("/refunds")
    public ResponseEntity<String> refund(@RequestBody RefundDTO refundDTO) {
        return paymentService.refund(refundDTO);
    }
}
