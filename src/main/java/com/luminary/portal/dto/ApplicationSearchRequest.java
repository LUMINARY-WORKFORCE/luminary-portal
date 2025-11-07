package com.luminary.portal.dto;

import lombok.Data;

@Data
public class ApplicationSearchRequest {
    private Integer page;
    private Integer size;
    private String status;
    private String sortBy = "appliedAt";
    private String sortDir = "desc";
}
