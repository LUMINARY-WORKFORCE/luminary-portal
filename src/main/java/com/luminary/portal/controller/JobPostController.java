package com.luminary.portal.controller;

import com.luminary.portal.dto.job.JobPostRequest;
import com.luminary.portal.dto.job.JobPostResponse;
import com.luminary.portal.dto.job.JobSearchRequest;
import com.luminary.portal.dto.job.JobSearchResponse;
import com.luminary.portal.entity.User;
import com.luminary.portal.entity.enums.JobStatus;
import com.luminary.portal.service.JobPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Job Management", description = "APIs for managing and searching job posts")
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostController {
    private final JobPostService jobService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<JobPostResponse>> getAllJobs() {
        List<JobPostResponse> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @Operation(summary = "Search jobs", description = "Search jobs with filters, pagination, and sorting (Employer/Job Seeker/Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Jobs fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'JOB_SEEKER', 'ADMIN')")
    public ResponseEntity<JobSearchResponse> searchJobs(
            @RequestBody JobSearchRequest jobSearchRequest
    ) {

        if(jobSearchRequest == null) {
            jobSearchRequest = new JobSearchRequest();
        }

        var result = jobService.searchJobs(jobSearchRequest);

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Create new job post (Employer only)")
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPostResponse> createJob(@Valid @RequestBody JobPostRequest jobPostRequest,
                                                     @AuthenticationPrincipal User currentUser) {
        JobPostResponse jobCreated = jobService.createJob(jobPostRequest, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(jobCreated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<?> deleteJob(@PathVariable Long id,
                                       @AuthenticationPrincipal User currentUser) {
        jobService.deleteJob(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update job status (Employer/Admin)")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<JobPostResponse> updateJobStatus(
            @PathVariable Long id,
            @RequestParam JobStatus status,
            @AuthenticationPrincipal User currentUser) {

        JobPostResponse updated = jobService.updateJobStatus(id, status, currentUser);
        return ResponseEntity.ok(updated);
    }
}
