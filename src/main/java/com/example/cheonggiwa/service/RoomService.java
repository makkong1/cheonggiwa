package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

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
                Room room = roomRepository.findRoomWithReviewsEntity(roomId); // 엔터티

                List<RoomReviewDTO> reviewDTOs = room.getReviews()
                                .stream()
                                .map(rr -> RoomReviewDTO.builder()
                                                .id(rr.getId())
                                                .content(rr.getContent())
                                                .username(rr.getUser().getUsername())
                                                .createdAt(rr.getCreatedAt())
                                                .build())
                                .toList(); // 엔터티 안에 리스트로있는 리뷰를 스트림을 이용해서 dto로 변환

                return RoomDetailDTO.builder()
                                .id(room.getId())
                                .roomName(room.getRoomName())
                                .price(room.getPrice())
                                .reviews(reviewDTOs)
                                .build();
        }

}
