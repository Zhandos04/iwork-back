package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;

public interface ReviewService {
    ReviewResponseDTO addReview(CreateReviewDTO createReviewDTO);
}
