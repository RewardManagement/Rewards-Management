package com.reward.controller;

import com.reward.entity.StudentBadges;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.StudentBadgesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/student-badges")
public class StudentBadgesController {

    @Autowired
    private StudentBadgesService studentBadgesService;

    // Assign a badge to a student by a teacher
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping("/assign")
    public ResponseEntity<ResponseModel<String>> assignBadgeToStudent(
            @RequestParam UUID studentId,
            @RequestParam UUID badgeId,
            @RequestParam UUID teacherId) {

        String response = studentBadgesService.assignBadgeToStudent(studentId, badgeId, teacherId);
        return ResponseEntity.ok(ResponseModel.success(200, "Badge assigned successfully!", response));
    }

    // Get all badges assigned to a student
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/student/{studentId}")
    
    public ResponseEntity<ResponseModel<List<StudentBadges>>> getBadgesByStudentId(@PathVariable UUID studentId) {
        ResponseModel<List<StudentBadges>> response = studentBadgesService.getBadgesByStudentId(studentId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get all badges assigned by a teacher
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ResponseModel<List<StudentBadges>>> getBadgesByTeacherId(@PathVariable UUID teacherId) {
        ResponseModel<List<StudentBadges>> response = studentBadgesService.getBadgesByTeacherId(teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Get all badges assigned to a student by a specific teacher
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping("/student/{studentId}/teacher/{teacherId}")
    public ResponseEntity<ResponseModel<List<StudentBadges>>> getBadgesByStudentAndTeacher(
            @PathVariable UUID studentId,
            @PathVariable UUID teacherId) {

        ResponseModel<List<StudentBadges>> response = studentBadgesService.getBadgesByStudentAndTeacher(studentId, teacherId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // Unassign (remove) a badge from a student
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/unassign")
    public ResponseEntity<ResponseModel<String>> removeBadgeFromStudent(
            @RequestParam UUID studentId,
            @RequestParam UUID badgeId) {

        String response = studentBadgesService.removeBadgeFromStudent(studentId, badgeId);
        return ResponseEntity.ok(ResponseModel.success(200, "Badge unassigned successfully!", response));
    }
}
