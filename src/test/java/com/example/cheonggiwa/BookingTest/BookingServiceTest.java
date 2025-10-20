// package com.example.cheonggiwa.BookingTest;

// import com.example.cheonggiwa.dto.BookingDateDTO;
// import com.example.cheonggiwa.entity.Booking;
// import com.example.cheonggiwa.entity.CheckStatus;
// import com.example.cheonggiwa.entity.Room;
// import com.example.cheonggiwa.entity.RoomStatus;
// import com.example.cheonggiwa.entity.User;
// import com.example.cheonggiwa.repository.BookingRepository;
// import com.example.cheonggiwa.repository.RoomRepository;
// import com.example.cheonggiwa.repository.UserRepository;
// import com.example.cheonggiwa.service.BookingService;

// import jakarta.transaction.Transactional;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.assertj.core.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @SpringBootTest
// @Transactional
// class BookingServiceTest {

// @Mock
// private BookingRepository bookingRepository;

// @Mock
// private RoomRepository roomRepository;

// @Mock
// private UserRepository userRepository;

// @InjectMocks
// private BookingService bookingService;

// private User user;
// private Room room;
// private Booking booking;

// @BeforeEach
// void setUp() {
// MockitoAnnotations.openMocks(this);

// user = User.builder()
// .id(1L)
// .username("tester")
// .build();

// room = Room.builder()
// .id(1L)
// .roomName("Ocean View")
// .roomStatus(RoomStatus.AVAILABLE)
// .build();

// booking = Booking.builder()
// .id(1L)
// .user(user)
// .room(room)
// .checkIn(LocalDateTime.of(2025, 10, 10, 14, 0))
// .checkOut(LocalDateTime.of(2025, 10, 12, 11, 0))
// .checkStatus(CheckStatus.CONFIRMED)
// .build();
// }

// @Test
// @DisplayName("예약 생성 성공")
// void createBooking_success() {
// // given
// when(userRepository.findById(1L)).thenReturn(Optional.of(user));
// when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
// when(bookingRepository.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(any(),
// any(), any()))
// .thenReturn(List.of());
// when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

// // when
// Booking result = bookingService.createBooking(1L, 1L,
// LocalDateTime.of(2025, 10, 10, 14, 0),
// LocalDateTime.of(2025, 10, 12, 11, 0));

// // then
// assertThat(result).isNotNull();
// assertThat(result.getCheckStatus()).isEqualTo(CheckStatus.CONFIRMED);
// verify(bookingRepository, times(1)).save(any(Booking.class));
// }

// @Test
// @DisplayName("예약 생성 실패 - 중복 예약 존재")
// void createBooking_fail_dueToOverlap() {
// // given
// when(userRepository.findById(1L)).thenReturn(Optional.of(user));
// when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
// when(bookingRepository.findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(any(),
// any(), any()))
// .thenReturn(List.of(booking));

// // then
// assertThatThrownBy(() -> bookingService.createBooking(1L, 1L,
// booking.getCheckIn(), booking.getCheckOut()))
// .isInstanceOf(IllegalStateException.class)
// .hasMessageContaining("예약이 존재");
// }

// @Test
// @DisplayName("예약 취소 성공")
// void cancelBooking_success() {
// // given
// when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
// when(bookingRepository.existsByRoomAndCheckStatusIn(any(),
// any())).thenReturn(false);

// // when
// bookingService.cancelBooking(1L);

// // then
// verify(bookingRepository).delete(booking);
// assertThat(room.getRoomStatus()).isEqualTo(RoomStatus.AVAILABLE);
// }

// @Test
// @DisplayName("유저 예약 조회 성공")
// void getUserBookings_success() {
// // given
// when(userRepository.existsById(1L)).thenReturn(true);
// when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

// // when
// List<BookingDateDTO> result = bookingService.getUserBookings(1L);

// // then
// assertThat(result).hasSize(1);
// assertThat(result.get(0).getCheckIn()).isEqualTo(booking.getCheckIn());
// }

// @Test
// @DisplayName("체크인 성공")
// void checkIn_success() {
// // given
// when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

// // when
// bookingService.checkIn(1L);

// // then
// assertThat(booking.getCheckStatus()).isEqualTo(CheckStatus.IN_PROGRESS);
// }

// @Test
// @DisplayName("체크아웃 성공")
// void checkOut_success() {
// // given
// when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

// // when
// bookingService.checkOut(1L);

// // then
// assertThat(booking.getCheckStatus()).isEqualTo(CheckStatus.COMPLETED);
// }
// }