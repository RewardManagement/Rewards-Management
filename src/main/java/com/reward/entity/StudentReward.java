package com.reward.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentReward {

    @EmbeddedId
    private StudentRewardId id; // Composite Primary Key

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private User user;  

    @ManyToOne
    @MapsId("rewardId")
    @JoinColumn(name = "reward_id", referencedColumnName = "id", nullable = false)
    private Rewards reward;

    @Column(name = "redeemed_at", nullable = false)
    private LocalDateTime redeemedAt;

}
