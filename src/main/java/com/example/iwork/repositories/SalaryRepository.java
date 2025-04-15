package com.example.iwork.repositories;

import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Salary;
import com.example.iwork.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Long countByUser(User user);
    List<Salary> findByUser(User user);

    List<Salary> findByUserAndApprovalStatus(User user, ApprovalStatus approvalStatus);
    List<Salary> findByCompanyId(Long companyId);
    List<Salary> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus);

    List<Salary> findByApprovalStatus(ApprovalStatus approvalStatus);

    @Query("SELECT s FROM Salary s WHERE s.job.id = :jobId AND s.location.id = :locationId " +
            "AND s.approvalStatus = 'APPROVED'")
    List<Salary> findByJobIdAndLocationId(@Param("jobId") Long jobId, @Param("locationId") Long locationId);


    // Получение зарплат по компании с фильтрацией статуса
    Page<Salary> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus, Pageable pageable);

    Page<Salary> findByCompanyIdAndExperienceContainingIgnoreCaseAndApprovalStatus(
            Long companyId, String experience, ApprovalStatus approvalStatus, Pageable pageable);

    // Получение зарплат с поиском по должности
    Page<Salary> findByCompanyIdAndPositionContainingIgnoreCaseAndApprovalStatus(
            Long companyId, String position, ApprovalStatus approvalStatus, Pageable pageable);

    // Получение зарплат с поиском по должности и фильтрацией по опыту
    Page<Salary> findByCompanyIdAndPositionContainingIgnoreCaseAndExperienceContainingIgnoreCaseAndApprovalStatus(
            Long companyId, String position, String experience, ApprovalStatus approvalStatus, Pageable pageable);
}