package com.reward.repository;

import com.reward.entity.Badges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BadgesRepository extends JpaRepository<Badges, UUID> {
    
    List<Badges> findByIsDeletedFalse(); // Fetch only active (not deleted) badges

    // Fetch all non-deleted badges
    List<Badges> findAllByIsDeletedFalse();

    // Fetch a badge by ID only if it's not deleted
    Optional<Badges> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByNameAndIsDeletedFalse(String name);

    

    @Modifying
    @Transactional
    @Query("UPDATE Badges b SET b.isDeleted = true WHERE b.id = :id")
    void softDeleteBadge(@Param("id") UUID id);
}
