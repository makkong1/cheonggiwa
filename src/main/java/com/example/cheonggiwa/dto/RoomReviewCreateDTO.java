package com.example.cheonggiwa.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomReviewCreateDTO {
    private Long roomId;
    private Long userId;
    private String content;
    private List<MultipartFile> images; // 업로드 이미지
}