sequenceDiagram
    participant Admin as 관리자
    participant WebApp as WEB
    participant DB as DB

    Admin ->> WebApp: 영화 정보 입력 (영화명, 가격, 상세정보)
    WebApp ->> WebApp: 입력된 정보 유효성 검증
    WebApp ->> DB: 영화 정보 요청
    DB ->> WebApp: 영화 목록 반환
    WebApp ->> Admin: 영화 등록 성공 메시지 반환






