# NBE2_2_Team6
가상의 극장을 운영하는 영화관 서비스입니다.

## 예시 예시

```mermaid
sequenceDiagram
    participant User as 사용자
    participant WebApp as WEB
    participant DB as DB
    participant JWT as JWT
    
    User ->> WebApp: 아이디, 비밀번호 입력
    WebApp ->> DB: 아이디 및 비밀번호 검증
    DB ->> WebApp: 검증 성공
    WebApp ->> JWT: JWT 생성
    WebApp ->> User: 로그인 성공 메시지
    
    Note over WebApp, User: 토큰 만료 시간 이후
    
    JWT -->> WebApp: JWT 만료
    WebApp ->> User: 자동 로그아웃
``` 

## 영화 정보 등록 

```mermaid
sequenceDiagram
    participant Admin as 관리자
    participant WebApp as WEB
    participant DB as DB

    Admin ->> WebApp: 영화 정보 입력 (영화명, 가격, 상세정보)
    WebApp ->> WebApp: 입력된 정보 유효성 검증
    WebApp ->> DB: 영화 정보 요청
    DB ->> WebApp: 영화 목록 반환
    WebApp ->> Admin: 영화 등록 성공 메시지 반환
```

## 영화 선택
```mermaid
sequenceDiagram
    participant User as 사용자
    participant WebApp as 웹
    participant DB as DB

    User ->> WebApp: 영화 선택 및 상영관, 시간 선택
    alt 회원
        WebApp ->> User: 좌석 선택 가능
        User ->> WebApp: 영화 정보, 상영관, 시간 전달
    else 비회원
        WebApp -->> User: 회원가입 유도
        User ->> WebApp: 회원가입 정보 입력
        WebApp ->> DB: 정보 등록
        DB -->> WebApp: 회원 가입 성공 메시지 반환
        WebApp ->> User: 가입 성공 메시지
    end
```

## 좌석 선택
```mermaid
sequenceDiagram
    participant User as 사용자
    participant WebApp as 웹
    participant DB as DB

    User ->> WebApp: 좌석 좌표 전송
    WebApp ->> DB: 유효한 좌석 확인
    alt 유효한 좌석
        DB -->> WebApp: 좌석을 예매 상태로 변경
        WebApp ->> User: 유효한 좌석 알림
    else 유효하지 않은 좌석
        DB -->> WebApp: 좌석 불가 알림
        WebApp ->> User: 좌석 선택 실패 메시지
    end

```

## 결제
```mermaid
sequenceDiagram
    participant User as 사용자
    participant WebApp as 웹
    participant DB as DB

    User ->> WebApp: 결제 방법 선택
    WebApp ->> DB: 결제 방법 및 금액 전달
    DB ->> WebApp: 결제 확인
    alt 결제 성공
        WebApp ->> User: 결제 완료 메시지
    else 결제 실패
        WebApp ->> User: 결제 실패 메시지
    end
```
