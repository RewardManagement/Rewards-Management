package com.reward.service;

import com.reward.entity.*;
import com.reward.exception.BadRequestException;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.repository.StudentBadgesRepository;
import com.reward.repository.BadgesRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentBadgesService {

    @Autowired
    private StudentBadgesRepository studentBadgesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BadgesRepository badgesRepository;

    // Assign a badge to a student by a teacher
    public ResponseModel<String >assignBadgeToStudent(UUID studentId, UUID badgeId, UUID teacherId) {
        if (studentId == null || badgeId == null || teacherId == null) {
            throw new BadRequestException("Student ID, Badge ID, and Teacher ID cannot be null.");
        }

        Optional<User> studentOpt = userRepository.findById(studentId);
        Optional<User> teacherOpt = userRepository.findById(teacherId);
        Optional<Badges> badgeOpt = badgesRepository.findByIdAndIsDeletedFalse(badgeId);

        if (studentOpt.isEmpty()) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        if (teacherOpt.isEmpty()) {
            throw new ResourceNotFoundException("Teacher not found with ID: " + teacherId);
        }
        if (badgeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Badge not found or has been deleted with ID: " + badgeId);
        }

        User student = studentOpt.get();
        User teacher = teacherOpt.get();
        Badges badge = badgeOpt.get();

        // Create a composite key
        StudentBadgesId studentBadgesId = new StudentBadgesId(student.getId(), badge.getId());

        // Check if the badge is already assigned to the student
        if (studentBadgesRepository.findById(studentBadgesId).isPresent()) {
            throw new AlreadyExistsException("Badge is already assigned to this student!");
        }

        // Create and save the StudentBadges entity
        StudentBadges studentBadge = StudentBadges.builder()
                .id(studentBadgesId)
                .student(student)
                .badge(badge)
                .teacher(teacher)
                .assignedAt(LocalDateTime.now())
                .build();

        studentBadgesRepository.save(studentBadge);
        return ResponseModel.success(200, "Badge assigned successfully!", null);
    }

    // Get all badges assigned to a student
public ResponseModel<List<StudentBadges>> getBadgesByStudentId(UUID studentId) {
        // Check if student exists
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }

        // Fetch assigned badges
        List<StudentBadges> badges = studentBadgesRepository.findByStudentId(studentId);

        // If no badges are found, return an error response
        if (badges.isEmpty()) {
            throw new ResourceNotFoundException("Badges not found with ID: " + studentId);
        }

        // Return success response with badge data
        return ResponseModel.success(HttpStatus.OK.value(), "Badges retrieved successfully!", badges);
    }

// Get all badges assigned by a teacher
public ResponseModel<List<StudentBadges>> getBadgesByTeacherId(UUID teacherId) {
    if (!userRepository.existsById(teacherId)) {
        throw new ResourceNotFoundException( "Teacher not found with ID: " + teacherId);
    }
    
    List<StudentBadges> badges = studentBadgesRepository.findByTeacherId(teacherId);
    
    if (badges.isEmpty()) {
        throw new ResourceNotFoundException( "No badges found assigned by teacher with ID: ");
    }
    
    return ResponseModel.success(200, "Badges assigned by teacher retrieved successfully!", badges);
}

// Get all badges assigned to a student by a specific teacher
public ResponseModel<List<StudentBadges>> getBadgesByStudentAndTeacher(UUID studentId, UUID teacherId) {
    if (!userRepository.existsById(studentId)) {
        throw new ResourceNotFoundException( "Student not found with ID: " + studentId);
    }
    
    if (!userRepository.existsById(teacherId)) {
        throw new ResourceNotFoundException( "Teacher not found with ID: " + teacherId);
    }
    
    List<StudentBadges> badges = studentBadgesRepository.findByStudentIdAndTeacherId(studentId, teacherId);
    
    if (badges.isEmpty()) {
        throw new ResourceNotFoundException( "No badges found assigned to student with ID: " + studentId + " by teacher with ID: " + teacherId);
    }
    
    return ResponseModel.success(200, "Badges assigned by teacher to student retrieved successfully!", badges);
}


    // Delete (unassign) a badge from a student
    public ResponseModel<String >removeBadgeFromStudent(UUID studentId, UUID badgeId) {
        if (studentId == null || badgeId == null) {
            throw new BadRequestException("Student ID and Badge ID cannot be null.");
        }

        StudentBadgesId id = new StudentBadgesId(studentId, badgeId);
        if (studentBadgesRepository.existsById(id)) {
            studentBadgesRepository.deleteById(id);
            return ResponseModel.success(200, "Badge removed successfully", null) ;
        } else {
            throw new ResourceNotFoundException("Badge assignment not found with Student ID: " + studentId + " and Badge ID: " + badgeId);
        }
    }
}
