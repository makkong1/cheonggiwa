package com.example.cheonggiwa.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.dto.RoomReviewCreateDTO;
import com.example.cheonggiwa.entity.Room;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.entity.RoomReview;
import com.example.cheonggiwa.repository.RoomRepository;
import com.example.cheonggiwa.repository.UserRepository;
import com.example.cheonggiwa.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

        private final ReviewRepository reviewRepository;
        private final RoomRepository roomRepository;
        private final UserRepository userRepository;

        // 리뷰 생성
        public RoomReviewDTO createReview(RoomReviewCreateDTO dto) {
                Room room = roomRepository.findById(dto.getRoomId())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));
                User user = userRepository.findById(dto.getUserId())
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

                RoomReview review = RoomReview.builder()
                                .room(room)
                                .user(user)
                                .content(dto.getContent())
                                .build();
                reviewRepository.save(review);

                return RoomReviewDTO.builder()
                                .id(review.getId())
                                .username(user.getUsername())
                                .content(review.getContent())
                                .createdAt(review.getCreatedAt())
                                .build();
        }

        // 리뷰 수정
        public RoomReviewDTO updateReview(Long reviewId, RoomReviewCreateDTO dto) {
                RoomReview review = reviewRepository.findById(reviewId)
                                .orElseThrow(() -> new RuntimeException("Review not found"));

                review.setContent(dto.getContent());

                RoomReview updated = reviewRepository.save(review);
                return RoomReviewDTO.builder()
                                .id(updated.getId())
                                .username(updated.getUser().getUsername())
                                .content(updated.getContent())
                                .createdAt(updated.getCreatedAt())
                                .build();
        }

        // 리뷰 삭제
        public void deleteReview(Long reviewId) {
                reviewRepository.deleteById(reviewId);
        }

        // Main.js용: 객실별 리뷰 조회 (기본 정보만)
        public List<Object[]> getReviewsForMain(Long roomId) {
                return reviewRepository.findReviewsForMain(roomId);
        }

}
