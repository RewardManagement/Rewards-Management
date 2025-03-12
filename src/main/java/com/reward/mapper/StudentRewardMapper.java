package com.reward.mapper;

import com.reward.dto.StudentRewardDTO;
import com.reward.entity.StudentReward;

public class StudentRewardMapper {
    
    public static StudentRewardDTO toDTO(StudentReward studentReward) {
        return new StudentRewardDTO(
                studentReward.getId().getStudentId(),
                studentReward.getId().getRewardId()
        );
    }

}
