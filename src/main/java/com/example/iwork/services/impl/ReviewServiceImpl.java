package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import com.example.iwork.entities.Company;
import com.example.iwork.entities.Review;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.CompanyNotFoundException;
import com.example.iwork.repositories.CompanyRepository;
import com.example.iwork.repositories.ReviewRepository;
import com.example.iwork.services.ReviewService;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;

    @Override
    @Transactional
    public ReviewResponseDTO addReview(CreateReviewDTO createReviewDTO) {
        Company company = companyRepository.findById(createReviewDTO.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        Review review = modelMapper.map(createReviewDTO, Review.class);
        review.setCompany(company);
        review.setUser(user);
        reviewRepository.save(review);
        return modelMapper.map(review, ReviewResponseDTO.class);
    }
}
