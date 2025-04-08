package com.reward.controller;

import com.reward.dto.PointHistoryDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.PointHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/point-history")
public class PointHistoryController {

    private final PointHistoryService pointHistoryService;

    public PointHistoryController(PointHistoryService pointHistoryService) {
        this.pointHistoryService = pointHistoryService;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping("/{studentId}")
    public ResponseEntity<ResponseModel<List<PointHistoryDTO>>> getPointHistoryByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(pointHistoryService.getPointHistoryByStudent(studentId));
    }
    
}
