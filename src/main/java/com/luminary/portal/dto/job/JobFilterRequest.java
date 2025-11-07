package com.luminary.portal.dto.job;

import lombok.Data;

@Data
public class JobFilterRequest {
    private String keyword;
    private String location;
    private String status;
    private String companyName;
}
