package com.example.cheonggiwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long roomId;
    private Long userId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String checkStatus; // WAITING, IN, OUT
    private LocalDateTime createdAt;
}