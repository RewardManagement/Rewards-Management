package com.reward.repository;

import com.reward.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailAndIsDeletedFalse(String email); 

    boolean existsByEmailAndIsDeletedFalse(String email);


    List<User> findByIsDeletedFalse();

    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    @Query("SELECT u FROM User u WHERE u.role.roleName = :roleName AND u.isDeleted = false")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :userId")
    void softDeleteUser(@Param("userId") UUID userId);

    @Query("SELECT u.profilePicture FROM User u WHERE u.id = :userId AND u.isDeleted = false")
    Optional<byte[]> findProfilePictureById(@Param("userId") UUID userId);
}
