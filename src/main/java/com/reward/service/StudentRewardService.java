package com.reward.service;

import com.reward.entity.StudentReward;
import com.reward.entity.StudentRewardId;
import com.reward.dto.StudentRewardDTO;
import com.reward.entity.Rewards;
import com.reward.entity.User;
import com.reward.repository.RewardsRepository;
import com.reward.repository.StudentRewardRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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

    // ✅ Get all rewards redeemed by a student (Directly from Entity)
    @Transactional
    public ResponseModel<List<StudentRewardDTO>> getStudentRewards(UUID studentId) {
        List<StudentRewardDTO> rewardDTO = studentRewardRepository.findStudentRewardsByStudentId(studentId);

        if (rewardDTO.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No rewards found for this student");
        }

        return new ResponseModel<>(200, "SUCCESS", "Rewards retrieved successfully", rewardDTO);
    }

    // ✅ Redeem a reward for a student (Using Entity)
    public ResponseModel<String> redeemReward(UUID studentId, UUID rewardId) {
        if (studentRewardRepository.existsById(new StudentRewardId(studentId, rewardId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reward already redeemed by this student");
        }
    
        Rewards reward = rewardsRepository.findById(rewardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reward not found"));
    
        User user = userRepository.findById(studentId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        StudentReward studentReward = StudentReward.builder()
                .id(new StudentRewardId(studentId, rewardId))
                .user(user)
                .reward(reward) // ✅ Use the retrieved reward
                .redeemedAt(LocalDateTime.now())
                .build();
    
        studentRewardRepository.save(studentReward);
    
        return new ResponseModel<>(201, "SUCCESS", "Reward redeemed successfully", null);
    }
    
}
