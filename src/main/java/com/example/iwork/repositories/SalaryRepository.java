package com.example.iwork.repositories;

import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Salary;
import com.example.iwork.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
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

}