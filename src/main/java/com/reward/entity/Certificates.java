package com.reward.entity;
 
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;
 
@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificates {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
 
    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_certificate_student"))
    private User student;
 
    @Column(nullable = false)
    private String fileName;
 
    @Lob
    @Column(name = "file_data", nullable = false)
    private byte[] fileData;
 
    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
 
    @Column(name = "points")
    private Integer points;
 
    @Column(name = "status", nullable = false)
    private String status; // "Pending", "Approved", "Rejected"
 
    @Column(name = "category")
    private String category;
 
    @ManyToOne
    @JoinColumn(name = "reviewer_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_certificate_reviewer"))
    private User reviewer;
 
    @Column(updatable = false)
    private LocalDateTime createdAt;
 
    private LocalDateTime updatedAt;
 
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
 