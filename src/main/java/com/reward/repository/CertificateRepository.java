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

    @Query("SELECT c FROM Certificates c WHERE c.student.id = :studentId AND c.isDeleted = false")
    List<Certificates> findByStudentId(@Param("studentId") UUID studentId);

    @Modifying
    @Query("UPDATE Certificates c SET c.isDeleted = true WHERE c.id = :certificateId")
    void softDeleteCertificate(@Param("certificateId") UUID certificateId);
    
    @Query("SELECT c.fileData FROM Certificates c WHERE c.id = :certificateId AND c.isDeleted = false")
    Optional<byte[]> findFileDataById(@Param("certificateId") UUID certificateId);
    
    @Query("SELECT c FROM Certificates c WHERE c.isDeleted = false")
    List<Certificates> findAllCertificates();

}
