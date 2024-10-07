package com.example.filmpass.service;

import com.example.filmpass.dto.PaymentDTO;
import com.example.filmpass.entity.PayStatus;
import com.example.filmpass.entity.PayType;
import com.example.filmpass.entity.Payment;
import com.example.filmpass.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import java.io.BufferedOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

@Service
@Transactional
@Log4j2
@AllArgsConstructor
public class PaymentService {
    private ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    //결제 요청 메서드 - 결제 요청하는 토스 API이용
    public ResponseEntity<String> payment(PaymentDTO paymentDTO) {
        URL url = null;
        StringBuilder responseBody = new StringBuilder();
        try {
            url = new URL("https://pay.toss.im/api/v2/payments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //API 사용할 때 필요한 정보 넘겨주기
            org.json.simple.JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderNo", paymentDTO.getOrderNo());   //여기 나중에 예매 번호 넣기
            jsonBody.put("amount", paymentDTO.getAmount());
            jsonBody.put("amountTaxFree", paymentDTO.getAmountTaxFree());
            jsonBody.put("productDesc", paymentDTO.getProductDesc());
            jsonBody.put("apiKey", "sk_test_w5lNQylNqa5lNQe013Nq");
            jsonBody.put("autoExecute", true);
            jsonBody.put("resultCallback", paymentDTO.getResultCallback());
            jsonBody.put("retUrl", "http://localhost:8080/pay/return");
            jsonBody.put("retCancelUrl", "http://localhost:8080/pay/cancel");

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

            //<---- DB에 저장하기 위해 entity에 넣기

            Payment payment = new Payment();
            payment.setAmount(paymentDTO.getAmount());
            // JSON 파싱: 요청 후 응답으로 받은 payToken 가져오기
            JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
            String token = jsonNode.get("payToken").asText();
            payment.setPayToken(token);

            //결제 완료 전이므로 PAY_STANDBY로 상태 설정
            payment.setStatus(PayStatus.PAY_STANDBY);

            payment.setOrderNo(paymentDTO.getOrderNo());


            //---->

            //Payment DB에 저장
            paymentRepository.save(payment);

            //사용자에게 결제 진행할 수 있는 URL 돌려주기
            String checkoutPage = jsonNode.get("checkoutPage").asText();
            return ResponseEntity.ok(checkoutPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 오류 메시지 반환
        }

    }

    //결제 정보 저장 메소드 - 결제 정보 불러오는 토스 API이용
    public void payComplete(String orderNo, String apiKey) {

        URL url = null;
        URLConnection connection = null;
        StringBuilder responseBody = new StringBuilder();
        try {
            url = new URL("https://pay.toss.im/api/v2/status");
            connection = url.openConnection();
            connection.addRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //API 사용할 때 필요한 정보 넘겨주기
            org.json.simple.JSONObject jsonBody = new JSONObject();
            jsonBody.put("orderNo", orderNo);
            jsonBody.put("apiKey", apiKey);

            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());

            bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
            bos.flush();
            bos.close();


            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();

            //<--- 응답받은 정보는 필요한 정보 파싱해서 entity에 저장
            JsonNode jsonNode2 = objectMapper.readTree(responseBody.toString());
            String paidTs = jsonNode2.get("paidTs").asText();
            Payment payment = paymentRepository.findByOrderNo(orderNo);
            payment.setStatus(PayStatus.PAY_COMPLETE);
            payment.setPaidTs(paidTs);

            //결제 정보가 TOSS_MOENY면 현금 , CARD면 카드
            if (jsonNode2.get("payMethod").asText() == "TOSS_MONEY"){
                PayType payType = PayType.CASH;
                payment.setPayType(payType);
            }else {
                PayType payType = PayType.CARD;
                payment.setPayType(payType);
            }
            //---->

            //Payment DB에 저장
            paymentRepository.save(payment);


        } catch (Exception e) {
            responseBody.append(e);
        }
        System.out.println(responseBody.toString());
    }

    //결제 취소 메서드 - 상태 결제 취소로 바뀌도록
    public void payCancle() {
        String orderNo = getLastOrderNo();
        Payment payment = paymentRepository.findByOrderNo(orderNo);
        payment.setStatus(PayStatus.PAY_CANCEL);

    }

    public String getLastOrderNo() {
        Optional<Payment> payment = paymentRepository.findTopByOrderByOrderNoDesc();
        return payment.map(Payment::getOrderNo).orElse(null); // 최신 orderNo 반환
    }
}

