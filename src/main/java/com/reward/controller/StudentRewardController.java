package com.reward.controller;

import com.reward.dto.StudentRewardDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.StudentRewardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // ✅ GET: Fetch all rewards redeemed by a student
   @GetMapping("/{studentId}/rewards")
    public ResponseEntity<ResponseModel<List<StudentRewardDTO>>> getStudentRewards(@PathVariable UUID studentId) {
        ResponseModel<List<StudentRewardDTO>> response = studentRewardService.getStudentRewards(studentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/{studentId}/rewards/{rewardId}/redeem")
    public ResponseEntity<ResponseModel<String>> redeemReward(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("rewardId") UUID rewardId) {
        
        ResponseModel<String> response = studentRewardService.redeemReward(studentId, rewardId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
