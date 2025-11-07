package com.luminary.portal.dto.job;

import com.luminary.portal.dto.search.SearchRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JobSearchRequest extends SearchRequest<JobFilterRequest> {
    private JobFilterRequest filter = new JobFilterRequest();
}
