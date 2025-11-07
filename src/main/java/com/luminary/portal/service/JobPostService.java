package com.luminary.portal.service;

import com.luminary.portal.config.PaginationProperties;
import com.luminary.portal.dto.job.*;
import com.luminary.portal.dto.PagedResponse;
import com.luminary.portal.dto.search.PaginationRequest;
import com.luminary.portal.dto.search.SortRequest;
import com.luminary.portal.entity.Company;
import com.luminary.portal.entity.JobPost;
import com.luminary.portal.entity.User;
import com.luminary.portal.entity.enums.JobStatus;
import com.luminary.portal.entity.enums.Role;
import com.luminary.portal.exception.ResourceNotFoundException;
import com.luminary.portal.exception.UnauthorizedOperationException;
import com.luminary.portal.repository.JobPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostService {
    private final JobPostRepository jobRepo;
    private final PaginationProperties paginationProps;

    public List<JobPostResponse> getAllJobs() {
        log.info("Fetching all jobs (admin only)");

        List<JobPost> jobs = jobRepo.findAll(Sort.by("postedDate").descending());
        ;

        return jobs.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public JobSearchResponse searchJobs(JobSearchRequest jobSearchRequest) {
        var pagination = jobSearchRequest.getPagination() != null ? jobSearchRequest.getPagination() : new PaginationRequest();
        var sortRequest = jobSearchRequest.getSort() != null ? jobSearchRequest.getSort() : new SortRequest();
        var filter = jobSearchRequest.getFilter() != null ? jobSearchRequest.getFilter() : new JobFilterRequest();

        int resolvedPage = (pagination.getPage() != null) ? pagination.getPage() : paginationProps.getDefaultPage();
        int resolvedSize = (pagination.getSize() != null) ? pagination.getSize() : paginationProps.getDefaultSize();

        pagination.setPage(resolvedPage);
        pagination.setSize(resolvedSize);

        String keyword = filter.getKeyword();
        String location = filter.getLocation();
        String status = filter.getStatus();
        String companyName = filter.getCompanyName();

        String sortBy = sortRequest.getBy() != null ? sortRequest.getBy() : "postedDate";
        String sortDir = sortRequest.getDirection();

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(resolvedPage, resolvedSize, sort);
        Page<JobPost> jobPage;

        if(keyword != null || location != null || status != null || companyName != null) {
            JobStatus jobStatus = status != null ? JobStatus.valueOf(status.toUpperCase()) : null;
            jobPage = jobRepo.findByFilters(keyword, location, jobStatus, companyName, pageable);
        } else {
            jobPage = jobRepo.findAll(pageable);
        }

        List<JobPostResponse> jobs = jobPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        var pagedResponse = PagedResponse.<JobPostResponse>builder()
                .content(jobs)
                .currentPage(jobPage.getNumber())
                .totalItems(jobPage.getTotalElements())
                .totalPages(jobPage.getTotalPages())
                .build();

        long totalActiveJobs = jobRepo.countByStatus(JobStatus.OPEN);

        return JobSearchResponse.builder()
                .results(pagedResponse)
                .totalActiveJobs(totalActiveJobs)
                .appliedFilters(Map.of(
                        "keyword", filter.getKeyword(),
                        "location", filter.getLocation(),
                        "status", filter.getStatus(),
                        "companyName", filter.getCompanyName()
                ))
                .build();
    }

    public JobPostResponse createJob(JobPostRequest jobPostRequest, User user) {
        Company company = user.getCompany();

        if(company == null) {
            throw new IllegalStateException("User is not associated with any company");
        }
        log.info("User {} is creating a job {} for the company {}", user.getUsername(), jobPostRequest.getTitle(), company.getName());

        JobPost job = JobPost.builder()
                .title(jobPostRequest.getTitle())
                .description(jobPostRequest.getDescription())
                .location(jobPostRequest.getLocation())
                .salary(jobPostRequest.getSalary())
                .status(jobPostRequest.getStatus())
                .postedBy(user)
                .company(company)
                .postedDate(LocalDateTime.now())
                .build();

        JobPost savedJob = jobRepo.save(job);
        log.info("Job {} created successfully with id {}", savedJob.getTitle(), savedJob.getId());

        return mapToResponse(savedJob);
    }

    public void deleteJob(Long jobId, User currentUser) {
        JobPost job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if(currentUser.getRole() == Role.EMPLOYER) {
            if(!job.getPostedBy().getId().equals(currentUser.getId())) {
                throw new UnauthorizedOperationException("You are not authorized to delete this job");
            }
            if(!job.getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new UnauthorizedOperationException("You are not authorized to delete this job");
            }
            if(job.getApplications() != null && !job.getApplications().isEmpty()) {
                throw new UnauthorizedOperationException("Cannot delete job with existing applications");
            }
        }

        log.info("User {} is deleting job {}", currentUser.getUsername(), job.getTitle());

        jobRepo.delete(job);

        log.info("Job {} deleted successfully", job.getTitle());
    }

    public JobPostResponse updateJobStatus(Long id, JobStatus status, User currentUser) {
        JobPost job = jobRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if(currentUser.getRole() == Role.EMPLOYER) {
            if(!job.getPostedBy().getId().equals(currentUser.getId())) {
                throw new UnauthorizedOperationException("You are not authorized to update this job");
            }
            if(!job.getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new UnauthorizedOperationException("You are not authorized to update this job");
            }
        }

        log.info("User {} is updating job {} status to {}", currentUser.getUsername(), job.getTitle(), status);

        job.setStatus(status);

        JobPost updatedJob = jobRepo.save(job);

        log.info("Job {} status updated successfully to {}", updatedJob.getTitle(), status);

        return mapToResponse(updatedJob);
    }

    private JobPostResponse mapToResponse(JobPost jobPost) {
        return JobPostResponse.builder()
                .id(jobPost.getId())
                .title(jobPost.getTitle())
                .description(jobPost.getDescription())
                .location(jobPost.getLocation())
                .salary(jobPost.getSalary())
                .status(jobPost.getStatus())
                .companyName(jobPost.getCompany() != null ? jobPost.getCompany().getName() : null)
                .postedBy(jobPost.getPostedBy() != null ? jobPost.getPostedBy().getName() : null)
                .postedDate(jobPost.getPostedDate())
                .build();
    }
}
