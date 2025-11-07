package com.luminary.portal.dto.job;

import com.luminary.portal.entity.enums.JobStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class JobPostRequest {
    @NotBlank(message = "Job title is required")
    private String title;

    @NotBlank(message = "Job description is required")
    private String description;

    @NotBlank(message = "Job location is required")
    private String location;

    @Positive(message = "Salary must be a positive number")
    private Double salary;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    private JobStatus status = JobStatus.OPEN;
}
