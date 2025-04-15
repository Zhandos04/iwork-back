package com.example.iwork.repositories;

import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Review;
import com.example.iwork.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Long countByUser(User user);

    List<Review> findByUser(User user);

    /**
     * Находит отзывы пользователя с определенным статусом
     *
     * @param user пользователь
     * @param approvalStatus статус отзыва
     * @return список отзывов
     */
    List<Review> findByUserAndApprovalStatus(User user, ApprovalStatus approvalStatus);

    /**
     * Находит отзывы по компании
     *
     * @param companyId ID компании
     * @return список отзывов
     */
    List<Review> findByCompanyId(Long companyId);

    /**
     * Находит отзывы по компании и статусу
     *
     * @param companyId ID компании
     * @param approvalStatus статус отзыва
     * @return список отзывов
     */
    List<Review> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus);

    List<Review> findByApprovalStatus(ApprovalStatus approvalStatus);

    Page<Review> findByCompanyIdAndApprovalStatus(Long companyId, ApprovalStatus approvalStatus, Pageable pageable);

    // Получение отзывов по компании с фильтрацией по рейтингу и статусу
    Page<Review> findByCompanyIdAndRatingAndApprovalStatus(Long companyId, Double rating, ApprovalStatus approvalStatus, Pageable pageable);
}
