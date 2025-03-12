package com.reward.service;

import com.reward.dto.CertificatesDTO;
import com.reward.entity.Certificates;
import com.reward.entity.User;
import com.reward.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificatesService {

    private final CertificateRepository certificateRepository;
    private final CertificatesMapper certificatesMapper;
    private final UserRepository userRepository;

    @Transactional
    public ResponseModel<List<CertificatesDTO>> getAllCertificates(UUID studentId, String category) {
        List<Certificates> certificates;

        if (studentId != null) {
            certificates = certificateRepository.findByStudentId(studentId);
        } else if (category != null) {
            certificates = certificateRepository.findByCategory(category);
        } else {
            certificates = certificateRepository.findByIsDeletedFalse();
        }

        if (certificates.isEmpty()) {
            throw new ResourceNotFoundException("No certificates found");
        }

        List<CertificatesDTO> certificatesDTO = certificates.stream()
                .map(certificatesMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseModel.success(200, "Certificates retrieved successfully", certificatesDTO);
    }

    @Transactional
    public ResponseModel<CertificatesDTO> getCertificateById(UUID certificateId) {
        Certificates certificates = certificateRepository.findByIdAndIsDeletedFalse(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        return ResponseModel.success(200, "Certificate retrieved successfully", certificatesMapper.toDTO(certificates));
    }

    @Transactional
    public ResponseModel<String> createCertificate(UUID studentId, MultipartFile file) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Certificates certificates = new Certificates();
        certificates.setStudent(student);
        certificates.setStatus("pending");

        // Handle file upload if provided
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
    public ResponseModel<byte[]> getCertificateFile(UUID certificateId) {
        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        if (certificate.getFileData() == null) {
            throw new ResourceNotFoundException("No certificate file found");
        }

        return ResponseModel.success(200, "Certificate file retrieved successfully", certificate.getFileData());
    }

    @Transactional
    public ResponseModel<String> deleteCertificateFile(UUID certificateId) {
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
        Certificates certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));

        certificateRepository.delete(certificate);

        return ResponseModel.success(200, "Certificate deleted successfully", null);
    }
}
