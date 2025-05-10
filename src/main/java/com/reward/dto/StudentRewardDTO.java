package com.reward.dto;

import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRewardDTO {
    private UUID studentId;
    private UUID rewardId; 
}
