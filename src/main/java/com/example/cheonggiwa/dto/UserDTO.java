package com.example.cheonggiwa.dto;

import com.example.cheonggiwa.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private LocalDateTime createdAt;

    private List<BookingDTO> bookings;

    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .createdAt(user.getCreatedAt())
                .bookings(user.getBookings() != null ? user.getBookings().stream()
                        .map(BookingDTO::fromEntity)
                        .collect(Collectors.toList())
                        : null)
                .build();
    }
}