package com.example.filmpass.entity;

public enum AgeRatingEnum {
    ALL,
    TWELVE_OLD,
    FIFTEEN_OLD,
    ADULT;

    public static AgeRatingEnum fromString(String rating) {
        switch (rating) {
            case "전체 관람가":
                return ALL;
            case "12세 관람가":
                return TWELVE_OLD;
            case "15세 관람가":
                return FIFTEEN_OLD;
            case "청소년 관람불가":
                return ADULT;
            default:
                return null; // 또는 기본값을 반환할 수 있음
        }
    }
}
