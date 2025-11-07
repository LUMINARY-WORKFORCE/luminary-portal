package com.luminary.portal.repository;

import com.luminary.portal.entity.Application;
import com.luminary.portal.entity.JobPost;
import com.luminary.portal.entity.User;
import com.luminary.portal.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByJobId(Long jobId, Pageable pageable);
    Page<Application> findByJobIdAndStatus(Long jobId, ApplicationStatus status, Pageable pageable);
    List<Application> findByApplicantId(Long applicantId);
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
}
