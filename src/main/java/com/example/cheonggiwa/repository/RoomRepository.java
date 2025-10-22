package com.example.cheonggiwa.repository;

// import com.example.cheonggiwa.dto.RoomDetailProjection;
import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.Room;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
        Optional<Room> findByRoomName(String roomName);

        // @Query("SELECT r FROM Room r " +
        // "LEFT JOIN FETCH r.reviews rev " +
        // "LEFT JOIN FETCH r.bookings b " +
        // "WHERE r.id = :roomId")
        // Optional<Room> findByIdWithReviewsAndBookings(@Param("roomId") Long roomId);

        // 1:n 관계를 한번에 2개 이상으로 조인해서 가져오면 MultipleBagFetchException 발생
        // 그래서 따로따로 가져오는 메서드로 만들어놓음
        @Query("SELECT r FROM Room r LEFT JOIN FETCH r.reviews WHERE r.id = :roomId")
        Room findRoomWithReviews(@Param("roomId") Long roomId);

        // @Query("SELECT r FROM Room r LEFT JOIN FETCH r.bookings WHERE r.id =:roomId")
        // 현재 또는 곧 있을 예약만 조회
        @Query("""
                            SELECT b FROM Booking b
                            WHERE b.room.id = :roomId
                              AND b.checkOut >= CURRENT_DATE
                        """)
        // Room findRoomWithBookings(@Param("roomId") Long roomId);
        List<Booking> findActiveBookingsByRoomId(@Param("roomId") Long roomId);

        // N+1 문제 해결: 예약과 연관된 Room, User를 한번에 로딩
        @Query("""
                        SELECT b FROM Booking b
                        LEFT JOIN FETCH b.room
                        LEFT JOIN FETCH b.user
                        WHERE b.room.id = :roomId
                          AND b.checkOut >= CURRENT_DATE
                        """)
        List<Booking> findActiveBookingsWithRoomAndUser(@Param("roomId") Long roomId);

        // N+1 문제 해결: 리뷰와 연관된 User를 한번에 로딩
        @Query("""
                        SELECT r FROM Room r
                        LEFT JOIN FETCH r.reviews rev
                        LEFT JOIN FETCH rev.user
                        WHERE r.id = :roomId
                        """)
        Room findRoomWithReviewsAndUsers(@Param("roomId") Long roomId);

        // 예약 생성시 방 잠금
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        @Query("select r from Room r where r.id = :roomId")
        Optional<Room> findByIdWithLock(@Param("roomId") Long roomId);

}
