package com.example.cheonggiwa.service;

import com.example.cheonggiwa.dto.UserDTO;
import com.example.cheonggiwa.entity.User;
import com.example.cheonggiwa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // 회원 생성
    public UserDTO createUser(User user) {
        User saved = userRepository.save(user);
        return UserDTO.fromEntity(saved);
    }

    // 회원 단건 조회
    public UserDTO getUser(Long id) {
        User user = userRepository.findWithBookingsById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        log.debug("user : {}", user);
        return UserDTO.fromEntity(user);
    }

    // 전체 회원 조회
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 회원 수정
    public UserDTO updateUser(Long id, User update) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        user.setUsername(update.getUsername());
        user.setPassword(update.getPassword());

        User saved = userRepository.save(user);
        return UserDTO.fromEntity(saved);
    }

    // 회원 삭제
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
