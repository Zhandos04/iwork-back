package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO addReview(CreateReviewDTO createReviewDTO, MultipartFile contractFile) throws FileUploadException;

    List<ReviewResponseDTO> getMyReviews(String status);

    void deleteReview(Long id);

    ReviewResponseDTO updateReview(Long id, CreateReviewDTO updateReviewDTO, MultipartFile contractFile) throws FileUploadException;
}
