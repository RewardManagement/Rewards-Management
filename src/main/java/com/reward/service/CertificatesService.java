package com.reward.service;

import com.reward.dto.CertificatesDTO;
import com.reward.entity.Certificates;
import com.reward.entity.User;
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

    @Transactional
    public ResponseModel<List<CertificatesDTO>> getAllCertificates(UUID studentId) {
        List<Certificates> certificates;

        if (studentId != null) {
            boolean studentExists = userRepository.existsById(studentId);
            if (!studentExists) {
                throw new ResourceNotFoundException("Student with ID " + studentId + " does not exist.");
            }

            certificates = certificateRepository.findByStudentId(studentId);

            if (certificates.isEmpty()) {
                throw new ResourceNotFoundException("No certificates found for student ID: " + studentId);
            }
        } else {
            certificates = certificateRepository.findAll();
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
    public ResponseModel<String> createCertificate(UUID studentId, String category, String status, int points, MultipartFile file) {
        if (studentId == null || category == null || status == null || points < 0) {
            throw new BadRequestException("Invalid input data");
        }

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Certificates certificates = new Certificates();
        certificates.setStudent(student);
        certificates.setCategory(category);
        certificates.setStatus(status);
        certificates.setPoints(points);

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
        if (!certificateRepository.existsById(certificateId)) {
            throw new ResourceNotFoundException("Certificate not found");
        }
        certificateRepository.softDeleteCertificate(certificateId);
        return ResponseModel.success(200, "Certificate soft deleted successfully", null);
    }

    @Transactional
    public ResponseModel<String> updateCertificate(UUID certificateId, String category, String status, int points, MultipartFile file) throws IOException {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID cannot be null");
        }

        Certificates certificate = certificateRepository.findByIdAndIsDeletedFalse(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        certificate.setCategory(category);
        certificate.setStatus(status);
        certificate.setPoints(points);

        if (file != null && !file.isEmpty()) {
            certificate.setFileData(file.getBytes());
        }

        certificateRepository.save(certificate);
        return ResponseModel.success(200, "Certificate updated successfully", null);
    }

    @Transactional
    public ResponseModel<String> uploadCertificateFile(UUID certificateId, MultipartFile file) throws IOException {
        if (certificateId == null || file == null || file.isEmpty()) {
            throw new BadRequestException("Certificate ID and file must be provided");
        }

        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        certificate.setFileData(file.getBytes());
        certificateRepository.save(certificate);

        return ResponseModel.success(201, "Certificate file uploaded successfully", null);
    }

    @Transactional
    public ResponseModel<byte[]> getCertificateFile(UUID certificateId) {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID cannot be null");
        }

        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        if (certificate.getFileData() == null) {
            throw new ResourceNotFoundException("No certificate file found");
        }

        return ResponseModel.success(200, "Certificate file retrieved successfully", certificate.getFileData());
    }

    @Transactional
    public ResponseModel<String> deleteCertificateFile(UUID certificateId) {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID cannot be null");
        }

        Certificates certificates = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        if (certificates.getFileData() == null) {
            throw new ResourceNotFoundException("No certificate file found");
        }

        certificates.setFileData(null);
        certificateRepository.save(certificates);

        return ResponseModel.success(200, "Certificate file removed successfully", null);
    }

    @Transactional
    public ResponseModel<String> deleteCertificate(UUID certificateId) {
        if (certificateId == null) {
            throw new BadRequestException("Certificate ID cannot be null");
        }

        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        certificateRepository.delete(certificate);

        return ResponseModel.success(200, "Certificate deleted successfully", null);
    }
    @Transactional
public ResponseModel<String> reviewCertificate(UUID certificateId, UUID reviewerId, int points, String status) {
    Certificates certificate = certificateRepository.findById(certificateId)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

    User reviewer = userRepository.findById(reviewerId)
            .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));

    // Check if reviewer has the correct role
    if (!(reviewer.getRole().getRoleName().equalsIgnoreCase("ADMIN") || 
          reviewer.getRole().getRoleName().equalsIgnoreCase("TEACHER"))) {
        throw new UnauthorizedException("Only Admins and Teachers can review certificates");
    }

    certificate.setReviewer(reviewer);
    certificate.setPoints(points);
    certificate.setStatus(status);
    certificateRepository.save(certificate);

    return ResponseModel.success(200, "Certificate reviewed successfully", null);
}
}