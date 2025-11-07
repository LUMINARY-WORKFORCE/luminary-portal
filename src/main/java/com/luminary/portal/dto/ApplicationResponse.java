package com.luminary.portal.dto;

import com.luminary.portal.entity.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;
    private Long jobId;
    private Long applicantId;
    private String jobTitle;
    private String applicantName;
    private String resumeUrl;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
