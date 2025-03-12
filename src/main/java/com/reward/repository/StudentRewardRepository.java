package com.reward.repository;

import com.reward.dto.StudentRewardDTO;
import com.reward.entity.StudentReward;
import com.reward.entity.StudentRewardId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRewardRepository extends JpaRepository<StudentReward, StudentRewardId> {

    // ✅ Find all rewards redeemed by a student
    @Query("SELECT new com.reward.dto.StudentRewardDTO(sr.id.studentId, sr.id.rewardId) FROM StudentReward sr WHERE sr.id.studentId = :studentId")
    List<StudentRewardDTO> findStudentRewardsByStudentId(UUID studentId);

    // ✅ Check if a student has already redeemed a specific reward
    boolean existsById(StudentRewardId studentRewardId);
}

