package com.luminary.portal.dto.search;

import lombok.Data;

@Data
public class PaginationRequest {
    private Integer page;
    private Integer size;
}
