package com.luminary.portal.entity;

import com.luminary.portal.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    private JobPost job;

    @ManyToOne
    @JoinColumn(name = "applicant_id", referencedColumnName = "id")
    private User applicant;

    private String resumeUrl;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    private LocalDateTime appliedAt = LocalDateTime.now();
}
