package com.luminary.portal.repository;

import com.luminary.portal.entity.JobPost;
import com.luminary.portal.entity.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    @Query("""
    SELECT jp FROM JobPost jp
    WHERE (:keyword IS NULL OR LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(jp.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    AND (:location IS NULL OR LOWER(jp.location) = LOWER(:location))
    AND (:status IS NULL OR jp.status = :status)
    AND (:companyName IS NULL OR LOWER(jp.company.name) LIKE LOWER(CONCAT('%', :companyName, '%')))
""")
    Page<JobPost> findByFilters(@Param("keyword") String keyword,
                                @Param("location") String location,
                                @Param("status") JobStatus status,
                                @Param("companyName") String companyName,
                                Pageable pageable);
    long countByStatus(JobStatus status);

}
