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
public class BookingDateDTO {
    private Long id;
    private String roomName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private String status;

    public static BookingDateDTO fromEntity(Booking booking) {
        return BookingDateDTO.builder()
                .id(booking.getId())
                .roomName(booking.getRoom().getRoomName())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .status(booking.getCheckStatus().name())
                .build();
    }
}