package com.reward.repository;

import com.reward.entity.Points;
import com.reward.entity.User;
import com.reward.dto.PointsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointsRepository extends JpaRepository<Points, UUID> {

    // ✅ Find points by student
    Optional<Points> findByStudent(User student);

    // ✅ Find points by student ID
    List<Points> findByStudentId(UUID studentId);

    // ✅ Get Points by Student ID as DTO
    @Query("SELECT new com.reward.dto.PointsDTO(p.id, p.student.id, p.pointBalance, p.totalPoints, p.totalSpent) " +
           "FROM Points p WHERE p.student.id = :studentId")
    Optional<PointsDTO> getPointsByStudentId(@Param("studentId") UUID studentId);

    // ✅ Get Points for ALL students under a specific Teacher
    @Query("SELECT new com.reward.dto.PointsDTO(p.id, p.student.id, p.pointBalance, p.totalPoints, p.totalSpent) " +
           "FROM Points p WHERE p.student.teacher.id = :teacherId")
    List<PointsDTO> getPointsByTeacherId(@Param("teacherId") UUID teacherId);
}
