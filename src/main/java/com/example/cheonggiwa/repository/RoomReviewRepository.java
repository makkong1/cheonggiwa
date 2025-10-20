package com.example.cheonggiwa.repository;

import com.example.cheonggiwa.entity.RoomReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomReviewRepository extends JpaRepository<RoomReview, Long> {
}

