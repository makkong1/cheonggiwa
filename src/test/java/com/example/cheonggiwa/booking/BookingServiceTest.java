package com.example.cheonggiwa.booking;

import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.CheckStatus;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.repository.BookingRepository;
import com.example.cheonggiwa.repository.RoomRepository;
import com.example.cheonggiwa.repository.UserRepository;
import com.example.cheonggiwa.service.BookingService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    private User testUser;
    private Room testRoom;

    @BeforeEach
    void setup() {
        // 이미 DB에 더미 데이터가 있다면 여기서 가져오기
        testUser = userRepository.findAll().get(0);
        testRoom = roomRepository.findAll().get(0);
    }

    @Test
    void testCreateBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        Booking booking = bookingService.createBooking(testUser.getId(), testRoom.getId(), checkIn, checkOut);

        assertNotNull(booking.getId());
        assertEquals(CheckStatus.WAITING, booking.getCheckStatus());
        assertEquals(testUser.getId(), booking.getUser().getId());
        assertEquals(testRoom.getId(), booking.getRoom().getId());
    }

    @Test
    void testIsAvailable() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(3);

        boolean availableBefore = bookingService.isAvailable(testRoom, checkIn, checkOut);
        assertTrue(availableBefore);

        bookingService.createBooking(testUser.getId(), testRoom.getId(), checkIn, checkOut);

        boolean availableAfter = bookingService.isAvailable(testRoom, checkIn, checkOut);
        assertFalse(availableAfter);
    }

    @Test
    void testCheckInAndCheckOut() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        Booking booking = bookingService.createBooking(testUser.getId(), testRoom.getId(), checkIn, checkOut);

        bookingService.checkIn(booking.getId());
        assertEquals(CheckStatus.IN, booking.getCheckStatus());

        bookingService.checkOut(booking.getId());
        assertEquals(CheckStatus.OUT, booking.getCheckStatus());
    }

    @Test
    void testCancelBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        Booking booking = bookingService.createBooking(testUser.getId(), testRoom.getId(), checkIn, checkOut);

        // 취소 시 실제 삭제
        bookingRepository.delete(booking);

        // DB에서 해당 예약이 더 이상 존재하지 않아야 함
        assertFalse(bookingRepository.findById(booking.getId()).isPresent());
    }


    @Test
    void testGetUserBookings() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        Booking booking = bookingService.createBooking(testUser.getId(), testRoom.getId(), checkIn, checkOut);

        // 상태를 IN으로 변경
        booking.setCheckStatus(CheckStatus.IN);
        bookingRepository.save(booking);

        List<Booking> userBookings = bookingService.getUserBookings(testUser.getId());
        userBookings.forEach(b -> System.out.println(b.getId()));

        assertTrue(userBookings.stream()
                .anyMatch(b -> b.getId().equals(booking.getId())));
    }
}
