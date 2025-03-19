package com.reward.service;
 
import com.reward.dto.CertificatesDTO;
import com.reward.entity.Certificates;
import com.reward.entity.User;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.BadRequestException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.UnauthorizedException;
import com.reward.mapper.CertificatesMapper;
import com.reward.repository.CertificateRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.util.List;
import java.util.UUID;
 
@Service
@RequiredArgsConstructor
public class CertificatesService {
 
    private final CertificateRepository certificateRepository;
    private final CertificatesMapper certificatesMapper;
    private final UserRepository userRepository;
    private final EventService eventService;
 
    @Transactional
    public ResponseModel<List<CertificatesDTO>> getAllCertificates(UUID studentId) {
        List<Certificates> certificates;
 
        if (studentId != null) {
            boolean studentExists = userRepository.existsByIdAndIsDeletedFalse(studentId);
            if (!studentExists) {
                throw new ResourceNotFoundException("Student not found");
            }
 
            certificates = certificateRepository.findByStudentIdAndIsDeletedFalse(studentId);
 
            if (certificates.isEmpty()) {
                throw new ResourceNotFoundException("No certificates found for this student");
            }
        } else {
            certificates = certificateRepository.findByIsDeletedFalse();
            if (certificates.isEmpty()) {
                throw new ResourceNotFoundException("No certificates found ");
            }
        }
 
        List<CertificatesDTO> certificateDTOs = certificates.stream()
                .map(certificatesMapper::toDTO)
                .toList();
 
        return ResponseModel.success(200, "Certificates retrieved successfully", certificateDTOs);
    }
 
    
 
    @Transactional
    public ResponseModel<CertificatesDTO> getCertificateById(UUID certificateId) {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID cannot be null");
        }
 
        Certificates certificates = certificateRepository.findByIdAndIsDeletedFalse(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
 
        return ResponseModel.success(200, "Certificate retrieved successfully", certificatesMapper.toDTO(certificates));
    }
 
    @Transactional
    public ResponseModel<String> createCertificate(UUID studentId, MultipartFile file) {
 
        if (studentId == null) {
            throw new BadRequestException("Student ID is required");
        }
    
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Certificate file is required");
        }
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
 
 
        boolean exists = certificateRepository.existsByStudentIdAndFileNameAndIsDeletedFalse(studentId, file.getOriginalFilename());
        if (exists) {
            throw new AlreadyExistsException("A certificate with the same file name already exists for this student.");
        }
 
        Certificates certificates = new Certificates();
        certificates.setStudent(student);
        certificates.setFileName(file.getOriginalFilename());
        certificates.setStatus("PENDING");
        if (file != null && !file.isEmpty()) {
            try {
                certificates.setFileData(file.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error processing file", e);
            }
        }
 
        certificateRepository.save(certificates);
        return ResponseModel.success(201, "Certificate created successfully", null);
    }
 
    @Transactional
    public ResponseModel<String> softDeleteCertificate(UUID certificateId) {
        if (!certificateRepository.existsByIdAndIsDeletedFalse(certificateId)) {
            throw new ResourceNotFoundException("Certificate not found");
        }
        certificateRepository.softDeleteCertificate(certificateId);
        return ResponseModel.success(200, "Certificate soft deleted successfully", null);
    }
 
 
    @Transactional
    public ResponseModel<String> reviewCertificate(UUID certificateId, UUID reviewerId, Integer points, String status, String category) {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID is required");
        }
        if (reviewerId == null) {
            throw new BadRequestException("Reviewer ID is required");
        }
        if (points == null || points < 0) {
            throw new BadRequestException("Valid points are required");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new BadRequestException("Status is required");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("Category is required");
        }
        Certificates certificate = certificateRepository.findByIdAndIsDeletedFalse(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
 
        User reviewer = userRepository.findByIdAndIsDeletedFalse(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
 
        if (!(reviewer.getRole().getRoleName().equalsIgnoreCase("ADMIN") ||
            reviewer.getRole().getRoleName().equalsIgnoreCase("TEACHER"))) {
            throw new UnauthorizedException("Only Admins and Teachers can review certificates");
        }
 
        certificate.setReviewer(reviewer);
        certificate.setPoints(points);
        certificate.setStatus(status);
        certificate.setCategory(category);
        certificateRepository.save(certificate);

        if ("APPROVED".equalsIgnoreCase(status)) {
            eventService.createEvent(certificate.getStudent().getId(), null, certificate.getId(), null, points);
        }
 
        return ResponseModel.success(200, "Certificate reviewed successfully", null);
    }
}