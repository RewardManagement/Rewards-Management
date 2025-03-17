package com.reward.repository;
 
import com.reward.entity.Points;
import com.reward.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.Optional;
import java.util.UUID;
import java.util.List;
 
@Repository
public interface PointsRepository extends JpaRepository<Points, UUID> {
 
   
    Optional<Points> findByStudent(User student);
 
    List<Points> findByStudentId(UUID studentId);
}
 