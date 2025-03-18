package com.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_badges")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentBadges {

    @EmbeddedId
    private StudentBadgesId id;  // Composite Key

    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id", nullable = false)
    private User student;  // Represents the student

    @ManyToOne
    @MapsId("badgeId")
    @JoinColumn(name = "badge_id", nullable = false)
    private Badges badge;  // Represents the badge

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;  // Represents the teacher

    @Builder.Default
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();
}