package com.example.cheonggiwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDetailDTO {
    private Long id;
    private String roomName;
    private Integer price;
    private String description;
    private List<BookingDateDTO> bookings; // 예약 현황 ??
    private List<RoomReviewDTO> reviews; // 리뷰 목록
    private String roomStatus;
}