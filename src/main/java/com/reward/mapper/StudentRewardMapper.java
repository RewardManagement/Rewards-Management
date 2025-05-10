package com.reward.mapper;

import com.reward.dto.StudentRewardDTO;
import com.reward.entity.StudentReward;
import com.reward.entity.StudentRewardId;
import com.reward.entity.User;
import com.reward.entity.Rewards;

public class StudentRewardMapper {

    // Convert Entity to DTO
    public static StudentRewardDTO toDTO(StudentReward studentReward) {
        return StudentRewardDTO.builder()
                .studentId(studentReward.getId().getStudentId())
                .rewardId(studentReward.getId().getRewardId())
                .build();
    }

    // Convert DTO to Entity
    public static StudentReward toEntity(StudentRewardDTO studentRewardDTO, User user, Rewards reward) {
        return StudentReward.builder()
                .id(new StudentRewardId(studentRewardDTO.getStudentId(), studentRewardDTO.getRewardId()))
                .user(user)
                .reward(reward)
                .build();
    }
}

