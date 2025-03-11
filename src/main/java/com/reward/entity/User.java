package com.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNo;

    @ManyToOne
    @JoinColumn(name = "teacher_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_user_teacher"))
    private User teacher;

    @Column(nullable = true)
    private String department;

    @Column(nullable = true)
    private Integer year;

    @Lob
    @Column(nullable = true)
    private byte[] profilePicture; 

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role; 

    @Builder.Default
    @Column(nullable = false)
    private boolean isDeleted = false;

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
