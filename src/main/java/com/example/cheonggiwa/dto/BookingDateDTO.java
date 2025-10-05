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
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public static BookingDateDTO fromEntity(Booking booking) {
        return BookingDateDTO.builder()
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .build();
    }
}