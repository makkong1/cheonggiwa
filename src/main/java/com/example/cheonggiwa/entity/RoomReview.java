package com.example.cheonggiwa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_review", uniqueConstraints = @UniqueConstraint(columnNames = { "room_id", "user_id" }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomReview {

    // =====================
    // 객실 리뷰 테이블
    // =====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 방의 리뷰인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // 누가 작성했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 한 줄 평 내용
    @Column(nullable = false, length = 255)
    private String content;

    // 작성 시각
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
