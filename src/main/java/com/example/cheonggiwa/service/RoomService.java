package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.BookingDateDTO;
import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

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
        public RoomDetailDTO detailRoom(Long roomId) {

                // 1. 리뷰만 조회
                Room roomWithReviews = roomRepository.findRoomWithReviews(roomId);
                if (roomWithReviews == null) {
                        throw new IllegalArgumentException("존재하지 않는 방입니다.");
                }

                // 2. 예약만 조회
                Room roomWithBookings = roomRepository.findRoomWithBookings(roomId);
                if (roomWithBookings == null) {
                        throw new IllegalArgumentException("존재하지 않는 방입니다.");
                }

                // 3. 리뷰 DTO 변환
                List<RoomReviewDTO> reviews = roomWithReviews.getReviews().stream()
                                .map(rr -> RoomReviewDTO.builder()
                                                .id(rr.getId())
                                                .content(rr.getContent())
                                                .username(rr.getUser().getUsername())
                                                .createdAt(rr.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                // 4. 예약 DTO 변환
                List<BookingDateDTO> bookings = roomWithBookings.getBookings().stream()
                                .map(BookingDateDTO::fromEntity)
                                .collect(Collectors.toList());

                // 5. Room 상태 DTO용 변환
                String roomStatus = roomWithBookings.getRoomStatus().name();

                // 6. DTO 반환
                return RoomDetailDTO.builder()
                                .id(roomWithReviews.getId()) // 리뷰, 예약 둘 다 같은 RoomId임
                                .roomName(roomWithReviews.getRoomName())
                                .price(roomWithReviews.getPrice())
                                .reviews(reviews)
                                .bookings(bookings)
                                .roomStatus(roomStatus)
                                .build();
        }

}
