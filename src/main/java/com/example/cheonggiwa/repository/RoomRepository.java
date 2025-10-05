package com.example.cheonggiwa.repository;

import com.example.cheonggiwa.entity.Room;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    @Query("SELECT r FROM Room r " +
            "LEFT JOIN FETCH r.reviews " +
            "LEFT JOIN FETCH r.bookings " +
            "WHERE r.id = :roomId")
    Room findRoomWithReviewsAndBookings(@Param("roomId") Long roomId);
}
