package com.luminary.portal.controller;

import com.luminary.portal.config.PaginationProperties;
import com.luminary.portal.dto.ApplicationResponse;
import com.luminary.portal.dto.ApplicationSearchRequest;
import com.luminary.portal.dto.ApplyRequest;
import com.luminary.portal.dto.PagedResponse;
import com.luminary.portal.entity.User;
import com.luminary.portal.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Application Management", description = "Endpoints for managing job applications")
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {
    private final ApplicationService applicationService;
    private final PaginationProperties paginationProps;

    @Operation(summary = "Apply to a job (Job Seeker only)")
    @PostMapping("/apply")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<ApplicationResponse> applyToJob(
            @Valid @RequestBody ApplyRequest applyRequest,
            @AuthenticationPrincipal User currentUser
            ) {
        log.info("Received job application request");

        ApplicationResponse applicationResponse = applicationService.applyToJob(applyRequest, currentUser);

        log.info("Job application processed successfully with ID: {}", applicationResponse.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationResponse);
    }

    @Operation(summary = "View all applications (Admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ApplicationResponse>> getAllApplications() {
        List<ApplicationResponse> apps = applicationService.getAllApplications();
        return ResponseEntity.ok(apps);
    }

    @Operation(summary = "Search applicants for a job (Employer/Admin)")
    @PostMapping("/job/{jobId}/search")
    @PreAuthorize("hasRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<PagedResponse<ApplicationResponse>> searchApplicationsForJob(
            @PathVariable Long jobId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody(required = false) ApplicationSearchRequest searchRequest
    ) {
        log.info("Received search request for applications for job ID: {}", jobId);

        if(searchRequest == null) {
            searchRequest = new ApplicationSearchRequest();
        }
        int resolvedPage = (searchRequest.getPage() != null) ? searchRequest.getPage() : paginationProps.getDefaultPage();
        int resolvedSize = (searchRequest.getSize() != null) ? searchRequest.getSize() : paginationProps.getDefaultSize();
        searchRequest.setPage(resolvedPage);
        searchRequest.setSize(resolvedSize);

        PagedResponse<ApplicationResponse> result = applicationService.searchApplicationsForJob(jobId, currentUser, searchRequest);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "View my applications (Job Seeker)")
    @GetMapping("/my-applications")
    @PreAuthorize("hasRole('JOB_SEEKER')")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User currentUser
    ) {
        log.info("Received request to fetch applications for user: {}", currentUser.getUsername());

        List<ApplicationResponse> applicationResponseList = applicationService.getApplicationsForApplicant(currentUser);

        log.info("Fetched {} applications for user: {}", applicationResponseList.size(), currentUser.getUsername());

        return ResponseEntity.ok(applicationResponseList);
    }
}
