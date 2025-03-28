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

    
    Optional<Points> findByStudent(User student);

    
    List<Points> findByStudentId(UUID studentId);

    @Query("SELECT p FROM Points p WHERE p.student.isDeleted = false")
    List<Points> findAllValidPoints();    
   
    @Query("SELECT new com.reward.dto.PointsDTO(p.id, p.student.name, p.pointBalance, p.totalPoints, p.totalSpent) " +
           "FROM Points p WHERE p.student.id = :studentId")
    Optional<PointsDTO> getPointsByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT new com.reward.dto.PointsDTO(p.id, p.student.name, p.pointBalance, p.totalPoints, p.totalSpent) " +
           "FROM Points p WHERE p.student.teacher.id = :teacherId")
    List<PointsDTO> getPointsByTeacherId(@Param("teacherId") UUID teacherId);
}
