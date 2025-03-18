package com.reward.service;

import com.reward.entity.StudentReward;
import com.reward.entity.StudentRewardId;
import com.reward.dto.StudentRewardDTO;
import com.reward.entity.Rewards;
import com.reward.entity.User;
import com.reward.exception.ResourceNotFoundException;
import com.reward.mapper.StudentRewardMapper;
import com.reward.exception.BadRequestException; 
import com.reward.repository.RewardsRepository;
import com.reward.repository.StudentRewardRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class StudentRewardService {
    private final UserRepository userRepository;
    private final StudentRewardRepository studentRewardRepository;
    private final RewardsRepository rewardsRepository;

    public StudentRewardService(StudentRewardRepository studentRewardRepository, RewardsRepository rewardsRepository, UserRepository userRepository) {
        this.studentRewardRepository = studentRewardRepository;
        this.rewardsRepository = rewardsRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ResponseModel<List<StudentRewardDTO>> getStudentRewards(UUID studentId) {
        // ✅ Check if the student exists in the User table and is not deleted
        boolean studentExists = userRepository.existsByIdAndIsDeletedFalse(studentId);
        if (!studentExists) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
    
        // ✅ Fetch the rewards from StudentRewardRepository
        List<StudentRewardDTO> rewardDTOs = studentRewardRepository.findStudentRewardsByStudentId(studentId);
    
        if (rewardDTOs.isEmpty()) {
            throw new ResourceNotFoundException("No rewards found for student with ID: " + studentId);
        }
    
        return new ResponseModel<>(200, "SUCCESS", "Rewards retrieved successfully", rewardDTOs);
    }    
    

    // ✅ Redeem a reward for a student (Using Entity)
    @Transactional
    public ResponseModel<String> redeemReward(UUID studentId, UUID rewardId) {
        if (studentRewardRepository.existsById(new StudentRewardId(studentId, rewardId))) {
            throw new BadRequestException("Reward already redeemed by this student.");
        }
    
        Rewards reward = rewardsRepository.findByIdAndIsDeletedFalse(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
    
        User user = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    
        // Use the mapper instead of manually building the entity
        StudentReward studentReward = StudentRewardMapper.toEntity(
                new StudentRewardDTO(studentId, rewardId), user, reward
        );
    
        studentRewardRepository.save(studentReward);
    
        return new ResponseModel<>(200, "SUCCESS", "Reward redeemed successfully", null);
    }
    
    
  
    
}
