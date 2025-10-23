package com.example.cheonggiwa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import com.example.cheonggiwa.dto.RoomReviewDTO;
import com.example.cheonggiwa.dto.RoomReviewCreateDTO;
import com.example.cheonggiwa.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping("/{roomId}")
    public ResponseEntity<RoomReviewDTO> createReview(@PathVariable Long roomId, @RequestBody RoomReviewCreateDTO dto) {
        RoomReviewDTO created = reviewService.createReview(dto);
        return ResponseEntity.ok(created);
    }

}
