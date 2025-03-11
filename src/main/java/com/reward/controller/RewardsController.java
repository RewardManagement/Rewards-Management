package com.reward.controller;

import com.reward.dto.RewardsDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;

    // ✅ GET /rewards - Retrieve all rewards
    @GetMapping
    public ResponseEntity<List<RewardsDTO>> getAllRewards() {
        List<RewardsDTO> rewards = rewardsService.getAllRewards();
        return ResponseEntity.status(HttpStatus.OK).body(rewards);
    }


    // ✅ GET /rewards/{rewardId} - Retrieve a specific reward by ID
    @GetMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<RewardsDTO>> getRewardById(@PathVariable UUID rewardId) {
        ResponseModel<RewardsDTO> response = rewardsService.getRewardById(rewardId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ✅ POST /rewards - Create a new reward
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel<RewardsDTO>> createReward(
            @RequestParam String name,  // Change from rewardName to name
            @RequestParam String description,  // Change from rewardDescription to description
            @RequestParam int points,  // Change from rewardPoints to points
            @RequestParam("image") MultipartFile image) {
    
        RewardsDTO rewardsDTO = new RewardsDTO();
        rewardsDTO.setName(name);
        rewardsDTO.setDescription(description);
        rewardsDTO.setPoints(points);
    
        ResponseModel<RewardsDTO> response = rewardsService.createReward(rewardsDTO, image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

    // ✅ PUT /rewards/{rewardId} - Update an existing reward
    @PutMapping(value = "/{rewardId}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel<RewardsDTO>> updateReward(
            @PathVariable UUID rewardId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int points,
            @RequestParam(value = "image", required = false) MultipartFile image) {  // Image is optional
    
        RewardsDTO rewardsDTO = new RewardsDTO();
        rewardsDTO.setName(name);
        rewardsDTO.setDescription(description);
        rewardsDTO.setPoints(points);
    
        ResponseModel<RewardsDTO> response = rewardsService.updateReward(rewardId, rewardsDTO, image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

    // ✅ DELETE /rewards/{rewardId} - Soft delete a reward
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<String>> softDeleteReward(@PathVariable UUID rewardId) {
        ResponseModel<String> response = rewardsService.softDeleteReward(rewardId);
        return ResponseEntity.ok(response);
    }

}
