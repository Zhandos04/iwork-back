package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateReviewDTO;

public interface GeminiService {
    String analyzeReview(CreateReviewDTO reviewDTO);
}