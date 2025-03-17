package com.reward.service;
 
import com.reward.dto.PointsDTO;
import com.reward.entity.Points;
import com.reward.entity.User;
import com.reward.mapper.PointsMapper;
import com.reward.repository.PointsRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
 
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
 
@Service
public class PointsService {
 
 
    private final PointsRepository pointsRepository;
    private final UserRepository userRepository;
 
    public PointsService(PointsRepository pointsRepository, UserRepository userRepository) {
        this.pointsRepository = pointsRepository;
        this.userRepository = userRepository;
    }
 
    // ✅ Get all students' points
    @Transactional
    public ResponseModel<List<PointsDTO>> getAllStudentsPoints() {
        List<Points> pointsList = pointsRepository.findAll();
        if (pointsList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No points records found.");
        }
 
        List<PointsDTO> pointsDTOList = pointsList.stream()
                .map(PointsMapper::toDTO)
                .collect(Collectors.toList());
 
        return new ResponseModel<>(200, "SUCCESS", "Points data retrieved successfully.", pointsDTOList);
    }
 
    // ✅ Get points for a specific student
    @Transactional
    public ResponseModel<PointsDTO> getStudentPoints(UUID studentId) {
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
 
        Points points = pointsRepository.findByStudent(student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Points record not found for this student."));
 
        return new ResponseModel<>(200, "SUCCESS", "Student points retrieved successfully.", PointsMapper.toDTO(points));
    }
 
    // ✅ Update student points
    @Transactional
    public ResponseModel<PointsDTO> updateStudentPoints(UUID studentId, int pointsToAdd, int pointsToSpend) {
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
 
        Points points = pointsRepository.findByStudent(student)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Points record not found for this student."));
 
        // Update points
        int newBalance = points.getPointBalance() + pointsToAdd - pointsToSpend;
        if (newBalance < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient point balance.");
        }
 
        points.setPointBalance(newBalance);
        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        points.setTotalSpent(points.getTotalSpent() + pointsToSpend);
        pointsRepository.save(points);
 
        return new ResponseModel<>(200, "SUCCESS", "Student points updated successfully.", PointsMapper.toDTO(points));
    }
 
    @Transactional
    public ResponseModel<PointsDTO> createStudentPoints(UUID studentId, Integer initialPoints) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found."));
    
        // ✅ Check if points record already exists
        if (pointsRepository.findByStudent(student).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Points record already exists for this student.");
        }
    
        // ✅ Create a new Points record with possible null initialPoints
        Points points = new Points(); // Generate new UUID
        points.setStudent(student);
        points.setPointBalance(initialPoints);  // This can now be null
        points.setTotalPoints(initialPoints);
        points.setTotalSpent(0);
    
        pointsRepository.save(points);
    
        return new ResponseModel<>(201, "CREATED", "Points record created successfully.", PointsMapper.toDTO(points));
    }
       
 
}