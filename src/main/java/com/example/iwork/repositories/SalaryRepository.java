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

    /**
     * Находит все записи о зарплатах, созданные указанным пользователем с указанным статусом
     *
     * @param user пользователь
     * @param approvalStatus статус одобрения
     * @return список записей о зарплатах
     */
    List<Salary> findByUserAndApprovalStatus(User user, ApprovalStatus approvalStatus);

    /**
     * Находит все записи о зарплатах для указанной компании
     *
     * @param companyId ID компании
     * @return список записей о зарплатах
     */
    List<Salary> findByCompanyId(Long companyId);

    /**
     * Находит записи о зарплатах по компании и статусу одобрения
     *
     * @param companyId ID компании
     * @param approvalStatus статус одобрения
     * @return список записей о зарплатах
     */
    List<Salary> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus);
}