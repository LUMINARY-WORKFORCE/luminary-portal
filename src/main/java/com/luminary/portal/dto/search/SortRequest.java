package com.luminary.portal.dto.search;

import lombok.Data;

@Data
public class SortRequest {
    private String by = "createdAt";
    private String direction = "desc";
}
