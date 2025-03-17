package com.reward.service;

import com.reward.dto.RewardsDTO;
import com.reward.entity.Rewards;
import com.reward.mapper.RewardsMapper;
import com.reward.repository.RewardsRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import java.util.UUID;


import java.util.stream.Collectors;

@Service
public class RewardsService {

    @Autowired
    private RewardsRepository rewardsRepository;

    @Transactional // Ensure LOB handling in a transaction
    public List<RewardsDTO> getAllRewards() {
        List<Rewards> rewards = rewardsRepository.findByIsDeletedFalse();
        return rewards.stream().map(RewardsMapper::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public ResponseModel<RewardsDTO> getRewardById(UUID rewardId) {
        Rewards reward = rewardsRepository.findById(rewardId)
                .orElseThrow(() -> new RuntimeException("Reward not found with ID: " + rewardId));
    
        RewardsDTO rewardDTO = RewardsMapper.toDTO(reward);
        return new ResponseModel<>( 200, "SUCCESS", "Reward retrieved successfully", rewardDTO);

    }

    @Transactional
    public ResponseModel<RewardsDTO> saveOrUpdateReward(UUID rewardId, RewardsDTO rewardsDTO, MultipartFile image) {
        try {
            Rewards reward;
    
            if (rewardId == null) {
                // ✅ Create new reward
                if (rewardsRepository.existsByNameAndIsDeletedFalse(rewardsDTO.getName())) {
                    return new ResponseModel<>(HttpStatus.CONFLICT.value(), "ERROR", "Reward with this name already exists", null);
                }
                reward = RewardsMapper.toEntity(rewardsDTO);
                reward.setIsDeleted(false); // Ensure it's not soft deleted
            } else {
                // ✅ Update existing reward
                reward = rewardsRepository.findByIdAndIsDeletedFalse(rewardId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reward not found or deleted"));
                
                // Update fields
                reward.setName(rewardsDTO.getName());
                reward.setDescription(rewardsDTO.getDescription());
                reward.setPoints(rewardsDTO.getPoints());
            }
    
            // ✅ Handle image (if provided)
            if (image != null && !image.isEmpty()) {
                reward.setImage(image.getBytes());
            }
    
            // ✅ Save the reward
            Rewards savedReward = rewardsRepository.save(reward);
    
            // ✅ Convert to DTO and return success response
            return new ResponseModel<>(rewardId == null ? HttpStatus.CREATED.value() : HttpStatus.OK.value(),
                    "SUCCESS",
                    rewardId == null ? "Reward created successfully" : "Reward updated successfully",
                    RewardsMapper.toDTO(savedReward));
        } catch (IOException e) {
            return new ResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", "Failed to process image", null);
        } catch (Exception e) {
            return new ResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", "Failed to save or update reward: " + e.getMessage(), null);
        }
    }
    
    

    @Transactional
    public ResponseModel<String> softDeleteReward(UUID rewardId) {
        Rewards reward = rewardsRepository.findByIdAndIsDeletedFalse(rewardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reward not found"));

        reward.setIsDeleted(true);  // Set isDeleted to true (soft delete)
        rewardsRepository.save(reward);  // Save the updated reward

        return new ResponseModel<>(
                200, "SUCCESS", "Reward soft deleted successfully", null
        );
    }
}

  