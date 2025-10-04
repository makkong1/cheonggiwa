package com.example.cheonggiwa.controller;

import com.example.cheonggiwa.dto.UserDTO;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.service.UserService;
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
