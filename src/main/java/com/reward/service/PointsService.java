package com.reward.service;

import com.reward.dto.PointsDTO;
import com.reward.entity.Points;
import com.reward.entity.User;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.BadRequestException;
import com.reward.mapper.PointsMapper;
import com.reward.repository.PointsRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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

    
    @Transactional
    public ResponseModel<List<PointsDTO>> getAllStudentsPoints() {
        List<Points> pointsList = pointsRepository.findAllValidPoints(); // Use the modified query
        if (pointsList.isEmpty()) {
            throw new ResourceNotFoundException("No points records found.");
        }
    
        List<PointsDTO> pointsDTOList = pointsList.stream()
                .map(PointsMapper::toDTO)
                .collect(Collectors.toList());
    
        return new ResponseModel<>(200, "SUCCESS", "Points data retrieved successfully.", pointsDTOList);
    }    

    
    @Transactional
    public ResponseModel<?> getPoints(UUID studentId, UUID teacherId) {
        if (studentId != null) {
            
            Optional<PointsDTO> points = pointsRepository.getPointsByStudentId(studentId);
            if (points.isEmpty()) {
                throw new ResourceNotFoundException("No points found for student ID: " + studentId);
            }
            return new ResponseModel<>(200, "SUCCESS", "Student points retrieved successfully.", points.get());
        } 
        else if (teacherId != null) {
            // Fetch points for all students under a specific teacher
            List<PointsDTO> pointsList = pointsRepository.getPointsByTeacherId(teacherId);
            if (pointsList.isEmpty()) {
                throw new ResourceNotFoundException("No students found under teacher ID: " + teacherId);
            }
            return new ResponseModel<>(200, "SUCCESS", "Points for all students under teacher retrieved successfully.", pointsList);
        } 
        else {
            throw new BadRequestException("Either studentId or teacherId must be provided.");
        }
    }    

    @Transactional
    public ResponseModel<PointsDTO> updateStudentPoints(UUID studentId, int pointsToAdd, int pointsToSpend) {
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found."));

        Points points = pointsRepository.findByStudent(student)
                .orElseThrow(() -> new ResourceNotFoundException("Points record not found for this student."));

        

        int newBalance = points.getPointBalance() + pointsToAdd - pointsToSpend;
        if (newBalance < 0) {
            throw new BadRequestException("Insufficient point balance.");
        }

        points.setPointBalance(newBalance);
        points.setTotalPoints(points.getTotalPoints() + pointsToAdd);
        points.setTotalSpent(points.getTotalSpent() + pointsToSpend);
        pointsRepository.save(points);

        return new ResponseModel<>(200, "SUCCESS", "Student points updated successfully.", PointsMapper.toDTO(points));
    }
}
