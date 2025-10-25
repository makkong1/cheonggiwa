package com.example.cheonggiwa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.cheonggiwa.entity.RoomReview;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface ReviewRepository extends JpaRepository<RoomReview, Long> {

    // N+1 문제 예방: User를 한 번에 로딩
    @Query("""
            SELECT rr FROM RoomReview rr
            LEFT JOIN FETCH rr.user u
            WHERE rr.room.id = :roomId
            """)
    List<RoomReview> findByRoomIdWithUser(@Param("roomId") Long roomId);

}
