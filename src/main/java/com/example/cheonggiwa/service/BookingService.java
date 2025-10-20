package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.BookingDateDTO;
import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.CheckStatus;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.entity.RoomStatus;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.repository.BookingRepository;
import com.example.cheonggiwa.repository.RoomRepository;
import com.example.cheonggiwa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    /**
     * 예약 생성
     */
    @Transactional
    public Booking createBooking(Long userId, Long roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 방 엔티티를 비관적 락(PESSIMISTIC_WRITE)으로 조회
        Room lockRoom = roomRepository.findByIdWithLock(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        // 예약 가능 여부 체크
        if (!isAvailable(lockRoom, checkIn, checkOut)) {
            throw new IllegalStateException("해당 날짜는 이미 예약이 존재합니다.");
        }

        // 예약 엔티티 생성 및 저장
        Booking booking = Booking.builder()
                .user(user)
                .room(lockRoom)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .checkStatus(CheckStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    /**
     * 예약 가능 여부 확인
     */
    public boolean isAvailable(Room lockRoom, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Booking> bookings = bookingRepository.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                lockRoom, checkOut, checkIn);
        return bookings.isEmpty();
    }

    /**
     * 예약 취소
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        // 1. 예약 조회
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        Room room = booking.getRoom(); // 예약된 방

        // 2. 예약 삭제
        bookingRepository.delete(booking);

        // 3. 해당 방 상태 업데이트
        // 다른 예약이 아직 남아있으면 OCCUPIED 유지, 아니면 AVAILABLE
        boolean hasOtherActiveBookings = bookingRepository.existsByRoomAndCheckStatusIn(
                room, List.of(CheckStatus.CONFIRMED, CheckStatus.WAITING));

        room.setRoomStatus(hasOtherActiveBookings ? RoomStatus.OCCUPIED : RoomStatus.AVAILABLE);
    }

    /**
     * 유저별 예약 내역 조회
     */
    @Transactional(readOnly = true)
    public List<BookingDateDTO> getUserBookings(Long userId) {
        // 존재 여부 체크
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 유저의 모든 예약을 조회하고 DTO로 변환
        List<Booking> bookings = bookingRepository.findByUserId(userId);
        return bookings.stream()
                .map(BookingDateDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 체크인 처리
     */
    public void checkIn(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        booking.setCheckStatus(CheckStatus.IN_PROGRESS);
    }

    /**
     * 체크아웃 처리
     * 스케줄러를 통해서 정해진시간에 WAITING으로 바꾸든 아니면 관리자가 수동으로 수정할수있게 해야한다
     */
    public void checkOut(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));
        booking.setCheckStatus(CheckStatus.COMPLETED);
    }
}
