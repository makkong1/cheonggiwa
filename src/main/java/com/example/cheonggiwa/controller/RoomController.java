package com.example.cheonggiwa.controller;

import com.example.cheonggiwa.dto.RoomDTO;
import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    public ResponseEntity<List<RoomDTO>> allRoom(){
        List<RoomDTO> allRoom = roomService.allRooms();
        return ResponseEntity.ok(allRoom);
    }

    @GetMapping("/{id}")
    // 방 상세화면 / id를 해야 pk를 해서 인덱스를 탄다. 물론 name에다가 인덱스를 만들면 탈수있을거같은데 일단 id로
    public ResponseEntity<RoomDetailDTO> roomDetail(@PathVariable Long room_id){
        RoomDetailDTO roomDetailDTO = roomService.detailRoom(room_id);
        return ResponseEntity.ok(roomDetailDTO);
    }

}
