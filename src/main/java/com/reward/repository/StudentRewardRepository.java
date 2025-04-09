package com.reward.repository;
import com.reward.entity.StudentReward;
import com.reward.entity.StudentRewardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRewardRepository extends JpaRepository<StudentReward, StudentRewardId> {

    
    @Query("SELECT sr FROM StudentReward sr WHERE sr.user.id = :studentId")
    List<StudentReward> findByStudentId(@Param("studentId") UUID studentId);

    // ✅ Check if a student has already redeemed a specific reward
    boolean existsById(StudentRewardId studentRewardId);
}

