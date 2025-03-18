package com.reward.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryDTO {
    private UUID id;
    private UUID studentId;
    private UUID eventId;
    private int pointsChanged;
    
}
