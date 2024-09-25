# NBE2_2_Team6
가상의 극장을 운영하는 영화관 서비스입니다.

## 로그인 시나리오

```mermaid
sequenceDiagram
    participant User as 사용자
    participant WebApp as WEB
    participant DB as DB
    participant Session as 세션
    
    User ->> WebApp: 아이디, 비밀번호 입력
    WebApp ->> DB: 아이디 및 비밀번호 검증
    DB ->> WebApp: 검증 성공
    WebApp ->> Session: 세션 생성
    WebApp ->> User: 로그인 성공 메시지
    
    Note over WebApp, User: 세션 만료 시간 이후
    
    Session -->> WebApp: 세션 만료
    WebApp ->> User: 자동 로그아웃


sequenceDiagram
    participant Admin as 관리자
    participant WebApp as WEB
    participant DB as DB

    Admin ->> WebApp: 영화 정보 입력 (영화명, 가격, 상세정보)
    WebApp ->> WebApp: 입력된 정보 유효성 검증
    WebApp ->> DB: 영화 정보 요청
    DB ->> WebApp: 영화 목록 반환
    WebApp ->> Admin: 영화 등록 성공 메시지 반환
