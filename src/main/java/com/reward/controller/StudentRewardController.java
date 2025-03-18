package com.reward.controller;

import com.reward.dto.StudentRewardDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.StudentRewardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentRewardController {

    private final StudentRewardService studentRewardService;

    public StudentRewardController(StudentRewardService studentRewardService) {
        this.studentRewardService = studentRewardService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/{studentId}/rewards")
    public ResponseEntity<ResponseModel<List<StudentRewardDTO>>> getStudentRewards(@PathVariable UUID studentId) {
        return ResponseEntity.ok(studentRewardService.getStudentRewards(studentId));
    }    

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/{studentId}/rewards/{rewardId}/redeem")
    public ResponseEntity<ResponseModel<String>> redeemReward(
            @PathVariable UUID studentId,
            @PathVariable UUID rewardId) {
        
        return ResponseEntity.ok(studentRewardService.redeemReward(studentId, rewardId));
    }
    
}
