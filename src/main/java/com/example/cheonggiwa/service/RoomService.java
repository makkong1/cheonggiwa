package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.BookingDateDTO;
import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.repository.RoomRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

        private final RoomRepository roomRepository;

        // 방 목록
        public List<RoomDTO> allRooms() {
                List<Room> allRooms = roomRepository.findAll();

                // Stream 이용해서 Room -> RoomDTO 변환
                return allRooms.stream()
                                .map(RoomDTO::fromEntity)
                                .toList();
        }

        // 방 상세화면
        @Transactional(readOnly = true)
        public RoomDetailDTO detailRoom(Long roomId) {

                // Projection 한 번에 리뷰 + 예약을 다 가져오는 방식
                // 장점: DB에서 한 번에 가져옴, 네트워크 호출 한 번
                // 단점: 결과가 Room × 리뷰 × 예약 의 Cartesian Product로 나옴
                // 예: 리뷰 5개, 예약 10개 → 50개의 row가 나옴 / 지금 둘다 가져오는걸 하면 10만건의 데이터가 발생
                // 그래서 기존에 방식으로 유지

                // 리뷰만 조회
                Room roomWithReviews = roomRepository.findRoomWithReviews(roomId);
                if (roomWithReviews == null) {
                        throw new IllegalArgumentException("존재하지 않는 방입니다.");
                }

                // 현재/곧 있을 예약 조회
                List<Booking> activeBookings = roomRepository.findActiveBookingsByRoomId(roomId);

                // 리뷰 DTO 변환
                List<RoomReviewDTO> reviews = roomWithReviews.getReviews().stream()
                                .map(rr -> RoomReviewDTO.builder()
                                                .id(rr.getId())
                                                .content(rr.getContent())
                                                .username(rr.getUser().getUsername())
                                                .createdAt(rr.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                // 예약 DTO 변환
                List<BookingDateDTO> bookings = activeBookings.stream()
                                .map(BookingDateDTO::fromEntity)
                                .collect(Collectors.toList());

                // Room 상태
                String roomStatus = roomWithReviews.getRoomStatus().name();

                // DTO 반환
                return RoomDetailDTO.builder()
                                .id(roomWithReviews.getId())
                                .roomName(roomWithReviews.getRoomName())
                                .price(roomWithReviews.getPrice())
                                .reviews(reviews)
                                .bookings(bookings)
                                .roomStatus(roomStatus)
                                .build();
        }

}
