package com.example.cheonggiwa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.cheonggiwa.entity.RoomReview;

@Repository
public interface ReviewRepository extends JpaRepository<RoomReview, Long> {

}
