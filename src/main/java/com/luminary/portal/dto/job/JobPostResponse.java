package com.luminary.portal.dto.job;

import com.luminary.portal.entity.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private Double salary;
    private JobStatus status;
    private String companyName;
    private String postedBy;
    private LocalDateTime postedDate;
}
