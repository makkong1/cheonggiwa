package com.example.cheonggiwa.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    // 특정 방 리뷰 조회
    @Transactional(readOnly = true)
    public List<RoomReviewDTO> getReviewsByRoom(Long roomId) {
        List<RoomReview> reviews = reviewRepository.findByRoomIdWithUser(roomId);
        return reviews.stream()
                .map(rr -> RoomReviewDTO.builder()
                        .id(rr.getId())
                        .username(rr.getUser().getUsername())
                        .content(rr.getContent())
                        .createdAt(rr.getCreatedAt())
                        .build())
                .toList();
    }

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

}
