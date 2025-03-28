package com.reward.controller;
 
import com.reward.dto.PointsDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.PointsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;


import org.springframework.web.bind.annotation.*;
 
import java.util.UUID;
 
@RestController
@RequestMapping("/api/points")
public class PointsController {
 
    private final PointsService pointsService;
 
    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }
 
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping
    public ResponseEntity<ResponseModel<?>> getPoints(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID teacherId) {
        return ResponseEntity.ok(pointsService.getPoints(studentId, teacherId));
    }
    
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/students/{studentId}")
    public ResponseEntity<ResponseModel<PointsDTO>> updateStudentPoints(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0") int pointsToAdd,
            @RequestParam(defaultValue = "0") int pointsToSpend) {
        return ResponseEntity.ok(pointsService.updateStudentPoints(studentId, pointsToAdd, pointsToSpend));
    }    
    
}