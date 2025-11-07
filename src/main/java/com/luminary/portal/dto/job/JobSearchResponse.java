package com.luminary.portal.dto.job;

import com.luminary.portal.dto.PagedResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSearchResponse {
    private PagedResponse<JobPostResponse> results;
    private long totalActiveJobs;
    private Map<String, Object> appliedFilters;
}
