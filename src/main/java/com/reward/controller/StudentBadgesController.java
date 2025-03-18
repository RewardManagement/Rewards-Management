package com.reward.controller;

import com.reward.entity.StudentBadges;
import com.reward.service.StudentBadgesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-badges")
public class StudentBadgesController {

    @Autowired
    private StudentBadgesService studentBadgesService;

    // Assign a badge to a student by a teacher
    @PostMapping("/assign")
    public ResponseEntity<String> assignBadgeToStudent(
            @RequestParam UUID studentId,
            @RequestParam UUID badgeId,
            @RequestParam UUID teacherId) {

        String response = studentBadgesService.assignBadgeToStudent(studentId, badgeId, teacherId);
        return ResponseEntity.ok(response);
    }

    // Get all badges assigned to a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentBadges>> getBadgesByStudentId(@PathVariable UUID studentId) {
        List<StudentBadges> badges = studentBadgesService.getBadgesByStudentId(studentId);
        return ResponseEntity.ok(badges);
    }

    

    // Get all badges assigned to a student by a specific teacher
    @GetMapping("/student/{studentId}/teacher/{teacherId}")
    public ResponseEntity<List<StudentBadges>> getBadgesByStudentAndTeacher(
            @PathVariable UUID studentId,
            @PathVariable UUID teacherId) {

        List<StudentBadges> badges = studentBadgesService.getBadgesByStudentAndTeacher(studentId, teacherId);
        return ResponseEntity.ok(badges);
    }

    // Unassign (remove) a badge from a student
    @DeleteMapping("/unassign")
    public ResponseEntity<String> removeBadgeFromStudent(
            @RequestParam UUID studentId,
            @RequestParam UUID badgeId) {

        String response = studentBadgesService.removeBadgeFromStudent(studentId, badgeId);
        return ResponseEntity.ok(response);
    }
}