package com.reward.service;

import com.reward.entity.*;
import com.reward.repository.StudentBadgesRepository;
import com.reward.repository.BadgesRepository;
import com.reward.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String assignBadgeToStudent(UUID studentId, UUID badgeId, UUID teacherId) {
        Optional<User> studentOpt = userRepository.findById(studentId);
        Optional<Badges> badgeOpt = badgesRepository.findById(badgeId);
        Optional<User> teacherOpt = userRepository.findById(teacherId);

        if (studentOpt.isPresent() && badgeOpt.isPresent() && teacherOpt.isPresent()) {
            User student = studentOpt.get();
            Badges badge = badgeOpt.get();
            User teacher = teacherOpt.get();

            // Create a composite key
            StudentBadgesId studentBadgesId = new StudentBadgesId(student.getId(), badge.getId());

            // Check if the badge is already assigned to the student
            if (studentBadgesRepository.findById(studentBadgesId).isPresent()) {
                return "Badge is already assigned to this student!";
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
            return "Badge assigned successfully!";
        } else {
            return "Student, Badge, or Teacher not found!";
        }
    }

    // Get all badges assigned to a student
    public List<StudentBadges> getBadgesByStudentId(UUID studentId) {
        return studentBadgesRepository.findByStudentId(studentId);
    }

    // Get all badges assigned by a teacher
    public List<StudentBadges> getBadgesByTeacherId(UUID teacherId) {
        return studentBadgesRepository.findByTeacherId(teacherId);
    }

    // Get all badges assigned to a student by a specific teacher
    public List<StudentBadges> getBadgesByStudentAndTeacher(UUID studentId, UUID teacherId) {
        return studentBadgesRepository.findByStudentIdAndTeacherId(studentId, teacherId);
    }

    // Delete (unassign) a badge from a student
    public String removeBadgeFromStudent(UUID studentId, UUID badgeId) {
        StudentBadgesId id = new StudentBadgesId(studentId, badgeId);
        if (studentBadgesRepository.existsById(id)) {
            studentBadgesRepository.deleteById(id);
            return "Badge unassigned successfully!";
        } else {
            return "Badge assignment not found!";
        }
    }
}