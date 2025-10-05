package com.example.cheonggiwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.example.cheonggiwa.entity.Booking;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long roomId;
    private String roomName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String checkStatus;
    private LocalDateTime createdAt;

    public static BookingDTO fromEntity(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .roomId(booking.getRoom() != null ? booking.getRoom().getId() : null)
                .roomName(booking.getRoom() != null ? booking.getRoom().getRoomName() : null)
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .checkStatus(booking.getCheckStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}