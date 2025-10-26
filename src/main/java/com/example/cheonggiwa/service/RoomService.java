package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.dto.RoomReviewDTO;
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

        // 방 목록 (Main.js용 - 필요한 필드만 조회)
        public List<RoomDTO> allRooms() {
                // Main.js에서 필요한 필드만 조회: id, roomName, price, roomStatus
                List<Object[]> roomData = roomRepository.findRoomsForMain();

                return roomData.stream()
                                .map(row -> RoomDTO.builder()
                                                .id((Long) row[0])
                                                .roomName((String) row[1])
                                                .price((Integer) row[2])
                                                .roomStatus(row[3] != null ? row[3].toString() : null)
                                                .build())
                                .toList();
        }

        // Main.js용: 방 목록 조회 (기본 정보만)
        public List<Object[]> allRoomsForMain() {
                return roomRepository.findRoomsForMain();
        }

        // 방 상세화면 (Main.js용 - 필요한 필드만 조회)
        @Transactional(readOnly = true)
        public RoomDetailDTO detailRoom(Long roomId) {
                // Main.js에서 필요한 필드만 조회: id, roomName, price, roomStatus, description, reviews
                Room roomWithReviews = roomRepository.findRoomWithReviewsAndUsers(roomId);
                if (roomWithReviews == null) {
                        throw new IllegalArgumentException("존재하지 않는 방입니다.");
                }

                // 리뷰 DTO 변환 (N+1 문제 해결됨)
                List<RoomReviewDTO> reviews = roomWithReviews.getReviews().stream()
                                .map(rr -> RoomReviewDTO.builder()
                                                .id(rr.getId())
                                                .content(rr.getContent())
                                                .username(rr.getUser().getUsername())
                                                .createdAt(rr.getCreatedAt())
                                                .build())
                                .collect(Collectors.toList());

                // Room 상태
                String roomStatus = roomWithReviews.getRoomStatus().name();

                // DTO 반환 (Main.js에서 필요한 필드만)
                return RoomDetailDTO.builder()
                                .id(roomWithReviews.getId())
                                .roomName(roomWithReviews.getRoomName())
                                .price(roomWithReviews.getPrice())
                                .roomStatus(roomStatus)
                                .description(null) // Room 엔티티에 description 필드가 없으므로 null 처리
                                .reviews(reviews)
                                .build();
        }

}
