package com.luminary.portal.service;

import com.luminary.portal.dto.ApplicationResponse;
import com.luminary.portal.dto.ApplicationSearchRequest;
import com.luminary.portal.dto.ApplyRequest;
import com.luminary.portal.dto.PagedResponse;
import com.luminary.portal.entity.Application;
import com.luminary.portal.entity.User;
import com.luminary.portal.entity.enums.ApplicationStatus;
import com.luminary.portal.entity.enums.Role;
import com.luminary.portal.exception.DuplicateApplicationException;
import com.luminary.portal.exception.ResourceNotFoundException;
import com.luminary.portal.exception.UnauthorizedOperationException;
import com.luminary.portal.repository.ApplicationRepository;
import com.luminary.portal.repository.JobPostRepository;
import com.luminary.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ApplicationService {
    private final ApplicationRepository appRepo;
    private final JobPostRepository jobRepo;
    private final UserRepository userRepo;

    public ApplicationResponse applyToJob(ApplyRequest applyRequest, User currentUser) {
        if(currentUser.getRole() != Role.JOB_SEEKER) {
            throw new UnauthorizedOperationException("Only job seekers can apply to jobs");
        }

        var jobId = applyRequest.getJobId();
        var job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        log.info("User {} is applying to job {}", currentUser.getUsername(), job.getTitle());

        boolean alreadyApplied = appRepo.existsByJobIdAndApplicantId(jobId, currentUser.getId());

        if(alreadyApplied) {
            throw new DuplicateApplicationException("You have already applied to this job");
        }

        var application = Application.builder()
                .job(job)
                .applicant(currentUser)
                .resumeUrl(applyRequest.getResumeUrl())
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();

        var savedApp = appRepo.save(application);

        log.info("Application submitted successfully for job {} by user {}", job.getTitle(), currentUser.getUsername());

        return mapToResponse(savedApp);
    }

    public List<ApplicationResponse> getAllApplications() {
        log.info("Fetching all job applications (admin only)");
        List<Application> applications = appRepo.findAll(Sort.by("appliedAt").descending());
        return applications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PagedResponse<ApplicationResponse> searchApplicationsForJob(Long jobId, User currentUser, ApplicationSearchRequest searchRequest) {
        var job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        if(currentUser.getRole() == Role.EMPLOYER) {
            if(!job.getPostedBy().getId().equals(currentUser.getId())) {
                throw new UnauthorizedOperationException("You are not authorized to view applications for this job");
            }
            if(!job.getCompany().getId().equals(currentUser.getCompany().getId())) {
                throw new UnauthorizedOperationException("You are not authorized to view applications for this job");
            }
        } else {
            throw new UnauthorizedOperationException("Only employers can view applications for jobs");
        }


        int page = searchRequest.getPage();
        int size = searchRequest.getSize();

        Sort sort =  searchRequest.getSortDir().equalsIgnoreCase("asc") ?
                Sort.by(searchRequest.getSortBy()).ascending() :
                Sort.by(searchRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Application> appPage;

        String status = searchRequest.getStatus();
        if(status != null && !status.isEmpty()) {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status.toUpperCase());
            appPage = appRepo.findByJobIdAndStatus(jobId, appStatus, pageable);
        } else {
            appPage = appRepo.findByJobId(jobId, pageable);
        }

        List<ApplicationResponse> applications = appPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        log.info("Retrieved {} applications for job {}", applications.size(), job.getTitle());

        return PagedResponse.<ApplicationResponse>builder()
                .content(applications)
                .currentPage(appPage.getNumber())
                .totalItems(appPage.getTotalElements())
                .totalPages(appPage.getTotalPages())
                .build();
    }

    public List<ApplicationResponse> getApplicationsForApplicant(User currentUser) {
        if(currentUser.getRole() != Role.JOB_SEEKER) {
            throw new UnauthorizedOperationException("Only job seekers can view their applications");
        }

        log.info("User {} is retrieving their applications", currentUser.getUsername());

        List<Application> applications = appRepo.findByApplicantId(currentUser.getId());

        log.info("Retrieved {} applications for user {}", applications.size(), currentUser.getUsername());

        return applications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ApplicationResponse mapToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .applicantId(application.getApplicant().getId())
                .jobTitle(application.getJob().getTitle())
                .applicantName(application.getApplicant().getName())
                .resumeUrl(application.getResumeUrl())
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
