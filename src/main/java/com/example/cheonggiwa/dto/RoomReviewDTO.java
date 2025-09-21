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
public class RoomReviewDTO {
    private Long id;
    private String username; // 리뷰 작성자
    private String content;  // 리뷰 내용
    private LocalDateTime createdAt;
}