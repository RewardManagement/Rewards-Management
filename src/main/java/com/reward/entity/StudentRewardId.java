package com.reward.entity;

import java.io.Serializable;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRewardId implements Serializable {
    
    @Column(name = "student_id")
    private UUID studentId;
    
    @Column(name = "reward_id")
    private UUID rewardId;
}
