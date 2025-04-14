package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import com.example.iwork.services.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping("/add")
    public ResponseEntity<Response<?>> createReview(@RequestBody @Valid CreateReviewDTO createReviewDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        ReviewResponseDTO reviewResponseDTO = reviewService.addReview(createReviewDTO);
        Response<ReviewResponseDTO> response = new Response<>(reviewResponseDTO, "Отзыв успешно создан", null, HttpStatus.CREATED.value());

        return ResponseEntity.ok(response);
    }
}