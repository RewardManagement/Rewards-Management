package com.reward.dto;
 
import lombok.*;
 
import java.util.UUID;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsDTO {
    private UUID id;
    private UUID studentId;
    private int pointBalance;
    private int totalPoints;
    private int totalSpent;
    
}