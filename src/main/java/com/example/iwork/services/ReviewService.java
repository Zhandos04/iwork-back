package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.requests.UpdateReviewStatusDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO addReview(CreateReviewDTO createReviewDTO, MultipartFile contractFile) throws FileUploadException;

    List<ReviewResponseDTO> getMyReviews(String status);

    void deleteReview(Long id);

    ReviewResponseDTO updateReview(Long id, CreateReviewDTO updateReviewDTO, MultipartFile contractFile) throws FileUploadException;

    List<ReviewResponseDTO> getAllReviews(String status);

    /**
     * Обновляет статус отзыва и добавляет комментарий администратора
     * Доступно только администраторам
     *
     * @param id ID отзыва
     * @param updateStatusDTO DTO с данными для обновления статуса
     * @return Обновленный DTO отзыва
     */
    ReviewResponseDTO updateReviewStatus(Long id, UpdateReviewStatusDTO updateStatusDTO);
}
