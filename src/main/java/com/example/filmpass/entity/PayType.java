package com.example.filmpass.entity;

public enum PayType{
    CARD("카드"),
    CASH("현금");

    private String description;

    PayType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }}