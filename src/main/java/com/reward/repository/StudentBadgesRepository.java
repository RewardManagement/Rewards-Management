package com.reward.repository;

import com.reward.entity.StudentBadges;
import com.reward.entity.StudentBadgesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentBadgesRepository extends JpaRepository<StudentBadges, StudentBadgesId> {

    // Find all badges assigned to a specific student
    List<StudentBadges> findByStudentId(UUID studentId);

    // Find all badges assigned by a specific teacher
    List<StudentBadges> findByTeacherId(UUID teacherId);

    // Find all badges assigned to a specific student by a specific teacher
    List<StudentBadges> findByStudentIdAndTeacherId(UUID studentId, UUID teacherId);

    // Check if a specific badge is already assigned to a student
    Optional<StudentBadges> findById(StudentBadgesId id);
}