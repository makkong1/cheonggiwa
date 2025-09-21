package com.example.cheonggiwa.repository;

import com.example.cheonggiwa.entity.Booking;
import com.example.cheonggiwa.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(
            Room room, LocalDate checkOut, LocalDate checkIn
    );
}
