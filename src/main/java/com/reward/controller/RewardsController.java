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

  
    @GetMapping
    public ResponseEntity<List<RewardsDTO>> getAllRewards() {
        List<RewardsDTO> rewards = rewardsService.getAllRewards();
        return ResponseEntity.status(HttpStatus.OK).body(rewards);
    }


   
    @GetMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<RewardsDTO>> getRewardById(@PathVariable UUID rewardId) {
        ResponseModel<RewardsDTO> response = rewardsService.getRewardById(rewardId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel<RewardsDTO>> saveOrUpdateReward(
            @RequestParam(required = false) UUID rewardId,  
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int points,
            @RequestParam(value = "image", required = false) MultipartFile image) {
    
        RewardsDTO rewardsDTO = new RewardsDTO();
        rewardsDTO.setName(name);
        rewardsDTO.setDescription(description);
        rewardsDTO.setPoints(points);
    
        ResponseModel<RewardsDTO> response = rewardsService.saveOrUpdateReward(rewardId, rewardsDTO, image);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

   
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<String>> softDeleteReward(@PathVariable UUID rewardId) {
        ResponseModel<String> response = rewardsService.softDeleteReward(rewardId);
        return ResponseEntity.ok(response);
    }

}
