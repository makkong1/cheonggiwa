package com.example.cheonggiwa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyAvailabilityDTO {
    private Long roomId;
    private LocalDate month; // month 의 1일로 표현
    private List<BlockedIntervalDTO> blockedIntervals;
}
