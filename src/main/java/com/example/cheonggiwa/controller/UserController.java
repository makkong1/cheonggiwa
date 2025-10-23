package com.example.cheonggiwa.controller;

import com.example.cheonggiwa.dto.LoginRequest;
import com.example.cheonggiwa.dto.UserDTO;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 생성
    @PostMapping
    public ResponseEntity<UserDTO> create(@RequestBody User user) {
        UserDTO dto = userService.createUser(user);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            UserDTO userDTO = userService.loginUser(request);
            session.setAttribute("loginUser", userDTO.getId());
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            // 서비스단에서 로그인 실패시 IllegalArgumentException 등으로 예외를 던진다고 가정
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 완료");
    }

    // 현재 로그인한 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("loginUser");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            UserDTO userDTO = userService.getUser(userId);
            return ResponseEntity.ok(userDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 회원 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    // 전체 회원 조회
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 회원 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody User user) {
        UserDTO updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
