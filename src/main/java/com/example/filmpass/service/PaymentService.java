package com.example.filmpass.service;

import com.example.filmpass.dto.PaymentDTO;
import com.example.filmpass.dto.RefundDTO;
import com.example.filmpass.entity.PayStatus;
import com.example.filmpass.entity.PayType;
import com.example.filmpass.entity.Payment;
import com.example.filmpass.entity.Reservation;
import com.example.filmpass.repository.PaymentRepository;
import com.example.filmpass.repository.ReservationRepository;
import com.example.filmpass.util.RefundStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONObject;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Transactional
@Log4j2
@AllArgsConstructor

public class PaymentService {
    private ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

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

            //예매 정보 있는지 확인
            Reservation reservation = reservationRepository.findByReserveId(paymentDTO.getReserveId());

            //결제 정보 있는지 확인
            Payment payment1 = paymentRepository.findByReservation(reservation);


            //예매 정보 없을 경우
            if(reservation == null){
                String body = "예매 정보가 없습니다";
                return new ResponseEntity<>(body, HttpStatus.OK);
            }
            //이미 결제 정보 있는 경우
            else if(payment1 != null) {
                String body = "이미 결제한 예매입니다.";
                return new ResponseEntity<>(body, HttpStatus.OK);
            }
            //결제 정보 없고 예매 정보만 있으면 결제 페이지 생성
            else if (reservation != null) {
                org.json.simple.JSONObject jsonBody = new JSONObject();
                Random random = new Random();
                jsonBody.put("orderNo", reservation.getReserveId() +  "movie" + random.nextInt(50000)+1500);

                //연령대에 따른 가격 설정
                if(reservation.getAdult() == 1){
                    jsonBody.put("amount", "15000");
                } else if (reservation.getYouth() == 1) {
                    jsonBody.put("amount", "10000");
                } else {
                    jsonBody.put("amount", "5000");
                }
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
                payment.setReservation(reservation);
                // JSON 파싱: 요청 후 응답으로 받은 payToken 가져오기
                JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
                String token = jsonNode.get("payToken").asText();
                payment.setPayToken(token);
                payment.setCreatedTs(LocalDateTime.now());
                //결제 완료 전이므로 PAY_STANDBY로 상태 설정
                payment.setStatus(PayStatus.PAY_STANDBY);

                //---->

                //Payment DB에 저장
                paymentRepository.save(payment);

                //사용자에게 결제 진행할 수 있는 URL 돌려주기
                String checkoutPage = jsonNode.get("checkoutPage").asText();
                return ResponseEntity.ok(checkoutPage);


            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // 오류 메시지 반환
        }
        return null;
    }


    //결제 정보 저장 메소드 - 결제 정보 불러오는 토스 API이용
    public void payComplete(String reserveId, String apiKey) {

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
            jsonBody.put("orderNo", reserveId);
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
            String time = jsonNode2.get("paidTs").asText();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime paidTs = LocalDateTime.parse(time, formatter);

            String amount = jsonNode2.get("amount").asText();
            //랜덤 값 만들었으므로 예매 번호만 뽑아내기
            String substring = reserveId.substring(0, reserveId.indexOf("m"));

            Reservation reservation = reservationRepository.findByReserveId(Long.valueOf(substring));
            log.info(reservation);
            Payment payment = paymentRepository.findByReservation(reservation);
            log.info(payment);
            payment.setStatus(PayStatus.PAY_COMPLETE);
            payment.setPaidTs(paidTs);
            payment.setAmount(Integer.valueOf(amount));
            payment.setAvailableRefundAmount(Integer.valueOf(amount)); // 초기화 추가

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
        Reservation reservation = getLastOrder();
        Payment payment = paymentRepository.findByReservation(reservation);
        payment.setStatus(PayStatus.PAY_CANCEL);

    }

    public Reservation getLastOrder() {
        Optional<Payment> payment = paymentRepository.findTopByOrderByCreatedTsDesc();
        return payment.map(Payment::getReservation).orElse(null);
    }

    // 환불 처리
    public ResponseEntity<String> refund(RefundDTO refundDTO) {
        try {

            // reserveId로 예매 정보 조회
            Reservation reservation = reservationRepository.findByReserveId(refundDTO.getReserveId());
            //결제 정보 조회
            Payment payment = paymentRepository.findByReservation(reservation);


            //예매 정보가 존재하지 않을 경우
            if(reservation == null){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예매 정보를 찾을 수 없습니다.");
            }
            // 결제 정보가 존재하지 않을 때 해당 메시지 반환함
            if (payment == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보를 찾을 수 없습니다.");
            }
            String payToken = payment.getPayToken();

            // 조회한 결제의 payToken 가져오기 없다면 오류 메시지 반환
            if (payToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("결제 토큰이 없습니다.");
            }

            // 환불 가능 금액 계산
            int availableRefundAmount = payment.getAvailableRefundAmount(); // 수정된 필드 사용

            // 환불 금액 초과 여부 확인
            if (refundDTO.getAmount() > availableRefundAmount) {
                // 상태를 FAILED로 업데이트(금액이 문제인 경우, 사용자 요청이 문제인 경우임)
                payment.setRefundStatus(RefundStatus.FAILED);
                paymentRepository.save(payment);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("환불 금액이 환불 가능한 금액을 초과합니다.");
            }



            // 환불 요청 날짜 설정
            refundDTO.setRefundRequestDate(LocalDateTime.now()); // 현재 시간으로 설정

            // 환불 요청 처리
            URL url = new URL("https://pay.toss.im/api/v2/refunds");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            // 요청 본문을 작성하기 위해 출력 가능하도록 설정함 아래 형식으로 작성하면 됨
            connection.setDoOutput(true);

            // JSON 요청 본문 작성
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("payToken", payToken); // 결제 시 받은 토큰 사용
            jsonBody.put("amount", refundDTO.getAmount());
            jsonBody.put("apiKey", "sk_test_w5lNQylNqa5lNQe013Nq");

            // 요청 본문 전송
            connection.getOutputStream().write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));

            // API 응답 처리
            int responseCode = connection.getResponseCode();
            StringBuilder responseBody = new StringBuilder();
            BufferedReader br;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            String line;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
            br.close();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 환불 성공 시 Payment 엔티티 업데이트
                payment.setRefundAmount(payment.getRefundAmount() + refundDTO.getAmount()); // 누적 환불 금액 업데이트
                payment.setAvailableRefundAmount(payment.getAmount() - payment.getRefundAmount()); // 환불 가능 금액 재계산
                payment.setRefundStatus(RefundStatus.REFUNDED);
                payment.setRefundRequestDate(refundDTO.getRefundRequestDate()); // 요청된 날짜 저장
                payment.setRefundDate(LocalDateTime.now()); // 처리된 날짜를 현재 시간으로 설정

                paymentRepository.save(payment); //업뎃된 내용 저장
                // db에는 정상적으로 작동되지만 출력화면에서는 남은 환불 가능 금액이 (결제금액 - 초기설정:0)으로 뜸 따라서 아래 기능 추가
                int showavailableRefundAmount = payment.getAvailableRefundAmount();
                // Toss API 응답과 남은 환불 가능 금액을 함께 반환
                String combinedResponse = responseBody.toString() + "\n남은 환불 가능 금액: " + showavailableRefundAmount + "원";

                return ResponseEntity.ok(combinedResponse); // 환불 성공 응답 반환
            } else {
                // 환불 실패 시 상태를 FAILED로 업데이트(서버, api가 문제인 경우, 요청 자체는 유효함)
                payment.setRefundStatus(RefundStatus.FAILED);
                paymentRepository.save(payment);

                return ResponseEntity.status(responseCode).body(responseBody.toString());
            }

        } catch (Exception e) {
            // 예외 발생 시, INTERNAL SERVER ERROR 응답 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}

