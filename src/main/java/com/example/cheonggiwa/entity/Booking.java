package com.example.cheonggiwa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = { "room_id", "check_in", "check_out" }))
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Builder
    public Booking(Long id, Room room,
            User user,
            LocalDateTime checkIn,
            LocalDateTime checkOut,
            CheckStatus checkStatus,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.room = room;
        this.user = user;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.checkStatus = checkStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // =====================
    // 예약 테이블
    // =====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================
    // 객실
    // =====================
    @ManyToOne(fetch = FetchType.LAZY)
    // @ManyToOne(fetch = FetchType.LAZY) 이코드 때문에 예약마다 개별적으로 room을 조회
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // =====================
    // 유저
    // =====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // =====================
    // 예약 날짜
    // =====================
    @Column(nullable = false)
    private LocalDateTime checkIn;

    @Column(nullable = false)
    private LocalDateTime checkOut;

    // =====================
    // 예약 상태
    // =====================
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // @Builder.Default
    private CheckStatus checkStatus = CheckStatus.WAITING;

    // =====================
    // 생성 / 수정 / 삭제
    // =====================
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // soft delete
    // private LocalDateTime deletedAt;
}