package com.example.cheonggiwa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    // =====================
    // 객실 테이블
    // =====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_name", nullable = false, unique = true)
    private String roomName;

    @Column(nullable = false)
    private Integer price;

    // 방 상태: AVAILABLE, OCCUPIED
    @Enumerated(EnumType.STRING)
    @Column(name = "room_status", nullable = false)
    @Builder.Default
    private RoomStatus roomStatus = RoomStatus.AVAILABLE;

    // 리뷰
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @Builder.Default
    private List<RoomReview> reviews = new ArrayList<>();

    // 예약 상태
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();
}
