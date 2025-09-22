package com.example.cheonggiwa.controller;

import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    // 예약 생성
    @PostMapping
    public Booking createBooking(
            @RequestParam Long userId,
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return bookingService.createBooking(userId, roomId, checkIn, checkOut);
    }

    // 예약 취소
    @DeleteMapping("/{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    // 유저별 예약 내역 조회
    @GetMapping("/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable Long userId) {
        return bookingService.getUserBookings(userId);
    }

    // 체크인 처리
    @PostMapping("/{bookingId}/checkin")
    public void checkIn(@PathVariable Long bookingId) {
        bookingService.checkIn(bookingId);
    }

    // 체크아웃 처리
    @PostMapping("/{bookingId}/checkout")
    public void checkOut(@PathVariable Long bookingId) {
        bookingService.checkOut(bookingId);
    }
}