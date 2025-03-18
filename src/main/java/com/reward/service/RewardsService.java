package com.reward.service;

import com.reward.dto.RewardsDTO;
import com.reward.entity.Rewards;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.BadRequestException; 
import com.reward.mapper.RewardsMapper;
import com.reward.repository.RewardsRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import java.util.stream.Collectors;

@Service
public class RewardsService {

    private final RewardsRepository rewardsRepository;

    // ✅ Constructor-based dependency injection
    public RewardsService(RewardsRepository rewardsRepository) {
        this.rewardsRepository = rewardsRepository;
    }

    @Transactional
    public ResponseModel<List<RewardsDTO>> getAllRewards() {
        List<Rewards> rewardsList = rewardsRepository.findByIsDeletedFalse(); // Fetch only non-deleted rewards
    
        if (rewardsList.isEmpty()) {
            throw new ResourceNotFoundException("No rewards found.");
        }
    
        List<RewardsDTO> rewardsDTOList = rewardsList.stream()
                .map(RewardsMapper::toDTO)
                .collect(Collectors.toList());
    
        return new ResponseModel<>(200, "SUCCESS", "Rewards retrieved successfully.", rewardsDTOList);
    }
    

    @Transactional
    public ResponseModel<RewardsDTO> getRewardById(UUID rewardId) {
        Rewards reward = rewardsRepository.findById(rewardId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward not found with ID: " + rewardId));
    
        RewardsDTO rewardDTO = RewardsMapper.toDTO(reward);
        
        return new ResponseModel<>(200, "SUCCESS", "Reward retrieved successfully",rewardDTO);
    }      

    @Transactional
    public ResponseModel<RewardsDTO> saveOrUpdateReward(UUID rewardId, RewardsDTO rewardsDTO, MultipartFile image) {
        Rewards reward;
    
        if (rewardId == null) {
            // ✅ Check if reward name already exists
            if (rewardsRepository.existsByNameAndIsDeletedFalse(rewardsDTO.getName())) {
                throw new AlreadyExistsException("Reward with this name already exists");
            }
            reward = RewardsMapper.toEntity(rewardsDTO);
            reward.setIsDeleted(false);
        } else {
            // ✅ Find and update existing reward
            reward = rewardsRepository.findByIdAndIsDeletedFalse(rewardId)
                    .orElseThrow(() -> new ResourceNotFoundException("Reward not found or deleted"));
    
            reward.setName(rewardsDTO.getName());
            reward.setDescription(rewardsDTO.getDescription());
            reward.setPoints(rewardsDTO.getPoints());
        }
    
        // ✅ Handle image (if provided)
        if (image != null && !image.isEmpty()) {
            try {
                reward.setImage(image.getBytes());
            } catch (IOException e) {
                throw new BadRequestException("Failed to process image"); // 🔹 Exception is now handled globally
            }
        }
    
        // ✅ Save the reward
        Rewards savedReward = rewardsRepository.save(reward);
    
        return new ResponseModel<>(200, "SUCCESS", rewardId == null ? "Reward created successfully" : "Reward updated successfully",RewardsMapper.toDTO(savedReward));
    
    }    
    
    @Transactional
    public ResponseModel<String> softDeleteReward(UUID rewardId) {
        Optional<Rewards> reward = rewardsRepository.findByIdAndIsDeletedFalse(rewardId);
    
        if (reward.isEmpty()) {  
            throw new ResourceNotFoundException("Reward not found or already deleted");  // ❌ Exception stops execution
        }
    
        rewardsRepository.softDeleteById(rewardId); 
    
        return new ResponseModel<>(200, "SUCCESS", "Reward soft deleted successfully", null);
    }
    
    
}

  