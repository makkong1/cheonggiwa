package com.example.cheonggiwa.repository;

import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.CheckStatus;
import com.example.cheonggiwa.entity.Room;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
        List<Booking> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
                        Room room, LocalDateTime checkOut, LocalDateTime checkIn);

        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
        List<Booking> findByUserId(@Param("userId") Long userId);

        // DB에서 유저별 체크인(IN) 상태 예약 조회
        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.checkStatus = :status")
        List<Booking> findByUserIdAndCheckStatus(@Param("userId") Long userId,
                        @Param("status") CheckStatus status);

        boolean existsByRoomAndCheckStatusIn(Room room, List<CheckStatus> statuses);
}
