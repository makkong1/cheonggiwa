package com.example.cheonggiwa.RoomTest;

import com.example.cheonggiwa.dto.RoomDetailDTO;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.repository.RoomRepository;
import com.example.cheonggiwa.service.RoomService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 롤백 (조회만 해도 붙이는 게 안전)
class RoomServiceTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void testDetailRoom_이미존재하는데이터조회() {
        // given : 이미 DB에 더미 데이터가 많으므로 그냥 첫 번째 방 가져오기
        Room room = roomRepository.findAll().get(0);

        // when : 해당 방 상세 조회
        RoomDetailDTO detailDTO = roomService.detailRoom(room.getId());

        // then : 기본 방 정보 검증
        assertNotNull(detailDTO, "RoomDetailDTO는 null이 아니어야 한다.");
        assertEquals(room.getId(), detailDTO.getId());
        assertEquals(room.getRoomName(), detailDTO.getRoomName());
        assertEquals(room.getPrice(), detailDTO.getPrice());

        // then : 리뷰 정보 검증 (개수는 DB 상태 따라 다르므로 느슨하게 검증)
        assertNotNull(detailDTO.getReviews(), "리뷰 리스트는 null이 아니어야 한다.");
        assertTrue(detailDTO.getReviews().size() >= 1,
                "리뷰는 최소 1개 이상 존재해야 한다. 현재 개수: " + detailDTO.getReviews().size());
    }
}
