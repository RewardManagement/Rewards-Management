package com.reward.repository;

import com.reward.entity.Rewards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RewardsRepository extends JpaRepository<Rewards, UUID> {

    @Modifying
    @Query("UPDATE Rewards r SET r.isDeleted = true WHERE r.id = :id")
    void softDeleteById(@Param("id") UUID id);


    
    List<Rewards> findByIsDeletedFalse();

    
    Optional<Rewards> findByIdAndIsDeletedFalse(UUID id);


    
    boolean existsByNameAndIsDeletedFalse(String name);
}




