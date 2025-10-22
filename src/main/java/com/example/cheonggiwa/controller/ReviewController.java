package com.example.cheonggiwa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.dto.RoomReviewCreateDTO;
import com.example.cheonggiwa.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 특정 방 리뷰 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<RoomReviewDTO>> getReviews(@PathVariable Long roomId) {
        List<RoomReviewDTO> reviews = reviewService.getReviewsByRoom(roomId);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<RoomReviewDTO> createReview(@RequestBody RoomReviewCreateDTO dto) {
        RoomReviewDTO created = reviewService.createReview(dto);
        return ResponseEntity.ok(created);
    }

}
