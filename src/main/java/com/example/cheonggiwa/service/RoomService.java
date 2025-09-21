package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    //방 목록
    public List<RoomDTO> allRooms() {
        List<Room> allRooms = roomRepository.findAll();

        // Stream 이용해서 Room -> RoomDTO 변환
        return allRooms.stream()
                .map(RoomDTO::fromEntity)
                .toList();
    }

    // 방 상세화면
    public RoomDetailDTO detailRoom(Long room_id) {
        Room room = roomRepository.findById(room_id)
                .orElseThrow(() -> new RuntimeException("Room not found: " + room_id));

        // Room → RoomDetailDTO 변환
        return RoomDetailDTO.builder()
                .id(room.getId())
                .roomName(room.getRoomName())
                .price(room.getPrice())
                // bookings, reviews는 나중에 서비스에서 채워줄 수 있음
                .build();
    }

}
