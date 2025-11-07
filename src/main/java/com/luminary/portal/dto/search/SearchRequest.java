package com.luminary.portal.dto.search;

import lombok.Data;

@Data
public class SearchRequest<TFilter> {
    private PaginationRequest pagination = new PaginationRequest();
    private SortRequest sort = new SortRequest();
    private TFilter filter;
}
