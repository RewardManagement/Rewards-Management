package com.reward.dto;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointHistoryDTO {
    private UUID id;
    private String fileName;
    private Integer pointsChanged;  
}
