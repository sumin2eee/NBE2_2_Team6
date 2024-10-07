package com.example.filmpass.controller;

import com.example.filmpass.dto.PaymentDTO;
import com.example.filmpass.entity.Payment;
import com.example.filmpass.repository.PaymentRepository;
import com.example.filmpass.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    //    "orderNo": 값 넣기,
    //	"productDesc": "테스트 상품",
    //    "autoExecute": true,
    //    "resultCallback": "",
    //    "amount": 100,
    //    "amountTaxFree": 0
    //}

    //결제 요청 페이지
    @PostMapping("/payments")
    public ResponseEntity<String> payment(@RequestBody PaymentDTO paymentDTO){
        return paymentService.payment(paymentDTO);
}

    //결제완료 후 돌아갈 페이지
    @GetMapping("/return")
    public String returnPage(@RequestParam String orderNo){
        //결제 정보 저장하는 메소드 호출
        String apiKey = "sk_test_w5lNQylNqa5lNQe013Nq";
        paymentService.payComplete(orderNo, apiKey);
        return "결제 완료"; //나중에 예매 정보 등 추가
    }

    //결제 취소 시 돌아갈 페이지
    @GetMapping("/cancel")
    public String canclePage(){
        paymentService.payCancle();
        return "결제 취소";
    }
}
