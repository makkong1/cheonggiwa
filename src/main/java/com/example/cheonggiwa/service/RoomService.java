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

                // 기존 방식 (N+1 문제 발생)
                // Room roomWithReviews = roomRepository.findRoomWithReviews(roomId);
                // Room → RoomReview : 1:N 관계
                // RoomReview → User : N:1 관계 (하지만 LAZY로 설정했기 때문에 즉시 로딩 X)
                // findRoomWithReviews() 여기서 user를 불러오는데 이때 N+1 문제 발생
                // 여기서 "user=프록시" => “필요하면 나중에 DB에서 가져올것이다” 하고 프록시 객체를 만들어 둔다.

                // if (roomWithReviews == null) {
                // throw new IllegalArgumentException("존재하지 않는 방입니다.");
                // }

                // List<Booking> activeBookings =
                // roomRepository.findActiveBookingsByRoomId(roomId);

                // N+1 문제 해결: 연관 엔티티를 한번에 로딩
                Room roomWithReviews = roomRepository.findRoomWithReviewsAndUsers(roomId);
                if (roomWithReviews == null) {
                        throw new IllegalArgumentException("존재하지 않는 방입니다.");
                }

                List<Booking> activeBookings = roomRepository.findActiveBookingsWithRoomAndUser(roomId);

                // 리뷰 DTO 변환 (이제 N+1 문제 없음)
                List<RoomReviewDTO> reviews = roomWithReviews.getReviews().stream()
                                .map(rr -> RoomReviewDTO.builder()
                                                .id(rr.getId())
                                                .content(rr.getContent())
                                                .username(rr.getUser().getUsername()) // 여기서 lazy로 설정한 user를 실제로 가져온다.
                                                                                      // 이때 N+1 문제 발생
                                                                                      // => 그래서
                                                                                      // findRoomWithReviewsAndUsers()메서드를
                                                                                      // 만들어서 한번에 로딩
                                                .createdAt(rr.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                // 예약 DTO 변환 (이제 N+1 문제 없음)
                List<BookingDateDTO> bookings = activeBookings.stream()
                                .map(BookingDateDTO::fromEntity) // room, user 이미 로딩됨
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
