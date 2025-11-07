package com.luminary.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplyRequest {
    @NotNull(message = "Job ID is required")
    Long jobId;

    @NotBlank(message = "Resume URL is required")
    String resumeUrl;
}
