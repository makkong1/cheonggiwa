package com.example.cheonggiwa.repository;

import com.example.cheonggiwa.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = { "bookings", "bookings.room" })
    Optional<User> findWithBookingsById(Long id);

    // Main.js용: 사용자 목록 조회 (기본 정보만)
    @Query("""
            SELECT u.id, u.username
            FROM User u
            ORDER BY u.id
            """)
    List<Object[]> findUsersForMain();

}