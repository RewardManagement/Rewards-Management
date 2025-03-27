package com.reward.controller;


import com.reward.dto.RewardsDTO;

import com.reward.responsemodel.ResponseModel;
import com.reward.service.RewardsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/rewards")
public class RewardsController {

    private final RewardsService rewardsService;

    // ✅ Constructor-based injection
    public RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<ResponseModel<List<RewardsDTO>>> getAllRewards() {
        return ResponseEntity.ok(rewardsService.getAllRewards());
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<RewardsDTO>> getRewardById(@PathVariable UUID rewardId) {
        return ResponseEntity.ok(rewardsService.getRewardById(rewardId));
    }    

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResponseModel<RewardsDTO>> saveOrUpdateReward(
            @RequestParam(required = false) UUID rewardId,
            @Valid @ModelAttribute("rewardsDTO") RewardsDTO rewardsDTO,  // Accept JSON as a string
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return ResponseEntity.ok(rewardsService.saveOrUpdateReward(rewardId, rewardsDTO, logo));
    }
   
    @PreAuthorize("hasAnyRole('ADMIN')")    
    @DeleteMapping("/{rewardId}")
    public ResponseEntity<ResponseModel<String>> softDeleteReward(@PathVariable UUID rewardId) {
        ResponseModel<String> response = rewardsService.softDeleteReward(rewardId);
        return ResponseEntity.ok(response);
    }

}
