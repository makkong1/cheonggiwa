package com.example.cheonggiwa.dto;

import com.example.cheonggiwa.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private Long id;
    private String roomName;
    private Integer price;

    public static RoomDTO fromEntity(Room room) {
        return RoomDTO.builder()
                .id(room.getId())
                .roomName(room.getRoomName())
                .price(room.getPrice())
                .build();
    }

}