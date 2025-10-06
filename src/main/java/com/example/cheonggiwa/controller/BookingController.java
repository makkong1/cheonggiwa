package com.example.cheonggiwa.controller;

import com.example.cheonggiwa.dto.BookingDateDTO;
import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    // 예약 생성
    @PostMapping
    public Booking createBooking(@RequestBody Map<String, Object> requestMap) {
        Long userId = Long.valueOf(requestMap.get("userId").toString());
        Long roomId = Long.valueOf(requestMap.get("roomId").toString());
        LocalDate checkIn = LocalDate.parse(requestMap.get("checkIn").toString());
        LocalDate checkOut = LocalDate.parse(requestMap.get("checkOut").toString());

        return bookingService.createBooking(userId, roomId, checkIn.atStartOfDay(), checkOut.atStartOfDay());
    }

    // 예약 취소
    @DeleteMapping("/{bookingId}")
    public void cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "예약 취소 중 오류가 발생했습니다.", e);
        }
    }

    // 유저별 예약 내역 조회
    @GetMapping("/user/{userId}")
    public List<BookingDateDTO> getUserBookings(@PathVariable Long userId) {
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