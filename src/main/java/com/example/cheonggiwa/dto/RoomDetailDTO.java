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
    private List<BookingDateDTO> bookings; // 예약 현황 ??
    private List<RoomReviewDTO> reviews; // 한줄평
    private String roomStatus;
}