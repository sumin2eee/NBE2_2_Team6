package com.example.filmpass.service;

import com.example.filmpass.dto.PaymentDTO;
import com.example.filmpass.dto.RefundDTO;
import com.example.filmpass.entity.Payment;
import com.example.filmpass.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;

@Service
@Transactional
@Log4j2
@AllArgsConstructor
public class PaymentService {
    private ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    public ResponseEntity<String> payment(PaymentDTO paymentDTO) {
        URL url = null;
        StringBuilder responseBody = new StringBuilder();
        try {
            //토스 API 사용
            url = new URL("https://pay.toss.im/api/v2/payments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            org.json.simple.JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderNo", paymentDTO.getOrderNo());   //여기 나중에 예매 번호 넣기
            jsonBody.put("amount", paymentDTO.getAmount());
            jsonBody.put("amountTaxFree", paymentDTO.getAmountTaxFree());
            jsonBody.put("productDesc", paymentDTO.getProductDesc());
            jsonBody.put("apiKey", "sk_test_w5lNQylNqa5lNQe013Nq");
            jsonBody.put("autoExecute", true);
            jsonBody.put("resultCallback", paymentDTO.getResultCallback());
            jsonBody.put("retUrl", paymentDTO.getRetUrl());
            jsonBody.put("retCancelUrl", paymentDTO.getRetCancelUrl());

            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
            bos.flush();
            bos.close();


            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            log.info(responseBody);
            br.close();

            Payment payment = new Payment();
            payment.setAmount(paymentDTO.getAmount());
            payment.setOrderNo(paymentDTO.getOrderNo()); // orderNo 저장 추가
            // JSON 파싱: payToken 가져오기
            JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
            String token = jsonNode.get("payToken").asText(); // token 필드만 가져옴
            payment.setPayToken(token);

            //Payment DB에 저장
            paymentRepository.save(payment);

            //사용자에게 결제 진행할 수 있는 URL 돌려주기
            String checkoutPage = jsonNode.get("checkoutPage").asText();
            return ResponseEntity.ok(checkoutPage); // 결과 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 오류 메시지 반환
        }
    }

    // 환불 처리
    public ResponseEntity<String> refund(RefundDTO refundDTO) {
        try {
            // orderNo로 결제 정보 조회
            Payment payment = paymentRepository.findByOrderNo(refundDTO.getOrderNo());
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("결제 정보를 찾을 수 없습니다.");
            }

            // 해당 결제의 payToken 가져오기
            String payToken = payment.getPayToken();

            // 환불 요청 처리
            URL url = new URL("https://pay.toss.im/api/v2/refunds");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // JSON 요청 본문 작성
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payToken", payToken);
            jsonBody.put("amount", refundDTO.getAmount());
            jsonBody.put("reason", "환불 사유"); // 환불 사유 추가

            // 요청 본문 전송
            connection.getOutputStream().write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));

            // API 응답 처리
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();

            // 환불 성공 시 Payment 엔티티 업데이트
            payment.setRefundAmount(refundDTO.getAmount());
            payment.setRefundStatus("REFUNDED");
            payment.setRefundDate(LocalDateTime.now());
            paymentRepository.save(payment); // 업데이트된 Payment 정보 저장

            return ResponseEntity.ok(responseBody.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

