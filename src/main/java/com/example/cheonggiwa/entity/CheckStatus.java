package com.example.cheonggiwa.entity;

public enum CheckStatus {
    WAITING, // 아직 입실 전
    CONFIRMED, // 예약 확정됨 (선택)
    IN_PROGRESS, // 실제 숙박 중
    COMPLETED; // 숙박 끝남
}