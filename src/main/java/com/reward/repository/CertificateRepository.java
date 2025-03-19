package com.reward.repository;
 
import com.reward.entity.Certificates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
import java.util.UUID;
 
@Repository
public interface CertificateRepository extends JpaRepository<Certificates, UUID> {
 
    Optional<Certificates> findByIdAndIsDeletedFalse(UUID id);
 
    List<Certificates> findByIsDeletedFalse();
 
    List<Certificates> findByStudentIdAndIsDeletedFalse(UUID studentId);
 
    boolean existsByIdAndIsDeletedFalse(UUID id);
 
    boolean existsByStudentIdAndFileNameAndIsDeletedFalse(UUID studentId, String fileName);
 
    @Modifying
    @Query("UPDATE Certificates c SET c.isDeleted = true WHERE c.id = :certificateId")
    void softDeleteCertificate(@Param("certificateId") UUID certificateId);
 
 
}
 