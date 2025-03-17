package com.reward.controller;
 
import com.reward.dto.PointsDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.PointsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/points")
public class PointsController {
 
    private final PointsService pointsService;
 
    public PointsController(PointsService pointsService) {
        this.pointsService = pointsService;
    }
 
   
    @GetMapping
    public ResponseEntity<ResponseModel<List<PointsDTO>>> getAllStudentsPoints() {
        ResponseModel<List<PointsDTO>> response = pointsService.getAllStudentsPoints();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
 
    
    @GetMapping("/students/{studentId}")
    public ResponseEntity<ResponseModel<PointsDTO>> getStudentPoints(@PathVariable UUID studentId) {
        ResponseModel<PointsDTO> response = pointsService.getStudentPoints(studentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
 
    
    @PutMapping("/students/{studentId}")
    public ResponseEntity<ResponseModel<PointsDTO>> updateStudentPoints(
            @PathVariable UUID studentId,
            @RequestParam(required = false, defaultValue = "0") int pointsToAdd,
            @RequestParam(required = false, defaultValue = "0") int pointsToSpend) {
        ResponseModel<PointsDTO> response = pointsService.updateStudentPoints(studentId, pointsToAdd, pointsToSpend);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
 
   
    @PostMapping("/students/{studentId}/points")
    public ResponseEntity<ResponseModel<PointsDTO>> createStudentPoints(
            @PathVariable UUID studentId,
            @RequestParam(required = false) Integer initialPoints) {  // Allow null values
    
        ResponseModel<PointsDTO> response = pointsService.createStudentPoints(studentId, initialPoints);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
}