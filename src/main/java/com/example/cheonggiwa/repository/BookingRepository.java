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

        boolean existsByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqualAndCheckStatusIn(
                        Room room, LocalDateTime checkOut, LocalDateTime checkIn, List<CheckStatus> statusList);

        // 지정한 기간과 겹치는 방의 예약 목록 조회 (상태 필터 포함)
        @Query("""
                                SELECT b FROM Booking b
                                WHERE b.room.id = :roomId
                                  AND b.checkStatus IN :statusList
                                  AND b.checkIn <= :end
                                  AND b.checkOut >= :start
                        """)
        List<Booking> findOverlappingBookingsInRange(
                        @Param("roomId") Long roomId,
                        @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end,
                        @Param("statusList") List<CheckStatus> statusList);

        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
        List<Booking> findByUserId(@Param("userId") Long userId);

        // DB에서 유저별 체크인(IN) 상태 예약 조회
        @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND b.checkStatus = :status")
        List<Booking> findByUserIdAndCheckStatus(@Param("userId") Long userId,
                        @Param("status") CheckStatus status);

        boolean existsByRoomAndCheckStatusIn(Room room, List<CheckStatus> statuses);

        // Main.js용: 예약 가용성 조회 (날짜 범위별)
        @Query("""
                        SELECT b.checkIn, b.checkOut, b.checkStatus
                        FROM Booking b
                        WHERE b.room.id = :roomId
                          AND b.checkIn <= :endDate
                          AND b.checkOut >= :startDate
                          AND b.checkStatus IN :statusList
                        ORDER BY b.checkIn
                        """)
        List<Object[]> findAvailabilityForMain(
                        @Param("roomId") Long roomId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("statusList") List<CheckStatus> statusList);

}
