package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.CreateReviewDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Company;
import com.example.iwork.entities.Review;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.CompanyNotFoundException;
import com.example.iwork.exceptions.ReviewNotFoundException;
import com.example.iwork.repositories.CompanyRepository;
import com.example.iwork.repositories.ReviewRepository;
import com.example.iwork.services.ReviewService;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final S3Service s3Service;

    private String validateAndUploadContractFile(MultipartFile file) throws FileUploadException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileUploadException("Файл слишком большой. Максимальный размер — 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType != null &&
                !(contentType.equals("application/pdf") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))) {
            throw new FileUploadException("Недопустимый тип файла. Разрешены только PDF и DOCX документы.");
        }

        try {
            return s3Service.uploadFile(file);
        } catch (IOException e) {
            throw new FileUploadException("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ReviewResponseDTO addReview(CreateReviewDTO createReviewDTO, MultipartFile contractFile) throws FileUploadException {
        String contractFileUrl = validateAndUploadContractFile(contractFile);
        Company company = companyRepository.findById(createReviewDTO.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Компания не найдена"));

        // Получаем текущего пользователя
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Создаем объект отзыва
        Review review = modelMapper.map(createReviewDTO, Review.class);
        if (contractFileUrl != null) {
            review.setContractDocumentUrl(contractFileUrl);
        }
        review.setCompany(company);
        review.setCreatedAt(LocalDateTime.now());
        review.setUser(user);

        // Устанавливаем автора в зависимости от настройки анонимности
        if (review.getAnonymous() == null || !review.getAnonymous()) {
            review.setAuthor(user.getFullName());
        }

        // Устанавливаем начальные значения
        review.setApprovalStatus(ApprovalStatus.PENDING);
        review.setHasAdminComment(false);
        review.setHelpfulCount(0);
        review.setNotHelpfulCount(0);
        review.setCommentsCount(0);

        // Форматируем дату на русском языке
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("LLLL yyyy", new Locale("ru"));
        String formattedDate = formatter.format(review.getCreatedAt());
        review.setDate(formattedDate.substring(0, 1).toUpperCase() +
                formattedDate.substring(1));

        // Сохраняем отзыв
        reviewRepository.save(review);

        // Преобразуем в DTO для ответа
        ReviewResponseDTO reviewResponseDTO = modelMapper.map(review, ReviewResponseDTO.class);
        reviewResponseDTO.setCompanyName(company.getName());
        reviewResponseDTO.setHasVerification(review.getContractDocumentUrl() != null &&
                !review.getContractDocumentUrl().isEmpty());
        reviewResponseDTO.setVerified(review.getContractDocumentUrl() != null &&
                !review.getContractDocumentUrl().isEmpty());

        return reviewResponseDTO;
    }

    @Override
    public List<ReviewResponseDTO> getMyReviews(String status) {
        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        List<Review> reviews;

        if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("Все")) {
            ApprovalStatus approvalStatus = mapStatusToApprovalStatus(status);
            reviews = reviewRepository.findByUserAndApprovalStatus(user, approvalStatus);
        } else {
            reviews = reviewRepository.findByUser(user);
        }

        return reviews.stream()
                .map(this::convertToReviewResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found with id: " + id));

        User currentUser = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        if (!review.getUser().getId().equals(currentUser.getId()) && !userService.isAdmin()) {
            throw new AccessDeniedException("You don't have permission to delete this review");
        }

        reviewRepository.delete(review);
    }
    private ApprovalStatus mapStatusToApprovalStatus(String status) {
        if (status == null) {
            return null;
        }

        return switch (status.toLowerCase()) {
            case "новые", "новый" -> ApprovalStatus.PENDING;
            case "одобренные", "одобрено" -> ApprovalStatus.APPROVED;
            case "отклоненные", "отказано" -> ApprovalStatus.REJECTED;
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
    private ReviewResponseDTO convertToReviewResponseDTO(Review review) {
        ReviewResponseDTO dto = modelMapper.map(review, ReviewResponseDTO.class);

        dto.setCompanyName(review.getCompany().getName());

        switch (review.getApprovalStatus()) {
            case APPROVED:
                dto.setStatus("Одобрено");
                break;
            case REJECTED:
                dto.setStatus("Отказано");
                break;
            case PENDING:
            default:
                dto.setStatus("Новый");
                break;
        }

        dto.setHasVerification(review.getContractDocumentUrl() != null && !review.getContractDocumentUrl().isEmpty());
        return dto;
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, CreateReviewDTO updateReviewDTO, MultipartFile contractFile) throws FileUploadException {
        // Находим существующий отзыв
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Отзыв не найден с ID: " + id));

        // Получаем текущего пользователя
        User currentUser = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        // Проверяем права на редактирование отзыва
        if (!existingReview.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("У вас нет прав на редактирование этого отзыва");
        }

        // Обработка файла договора, если он предоставлен
        if (contractFile != null && !contractFile.isEmpty()) {
            String contractFileUrl = validateAndUploadContractFile(contractFile);

            // Если был загружен новый файл договора и существует старый, удаляем его
            if (existingReview.getContractDocumentUrl() != null &&
                    !existingReview.getContractDocumentUrl().isEmpty()) {
                try {
                    s3Service.deleteFromS3(existingReview.getContractDocumentUrl());
                } catch (Exception e) {
                    throw new FileUploadException("Ошибка при удаление файла: " + e.getMessage());
                }
            }
            existingReview.setContractDocumentUrl(contractFileUrl);
        }

        // Проверяем, изменилась ли компания
        if (!existingReview.getCompany().getId().equals(updateReviewDTO.getCompanyId())) {
            Company company = companyRepository.findById(updateReviewDTO.getCompanyId())
                    .orElseThrow(() -> new CompanyNotFoundException("Компания не найдена"));
            existingReview.setCompany(company);
        }

        // Обновляем поля отзыва
        existingReview.setTitle(updateReviewDTO.getTitle());
        existingReview.setBody(updateReviewDTO.getBody());
        existingReview.setPros(updateReviewDTO.getPros());
        existingReview.setCons(updateReviewDTO.getCons());
        existingReview.setAdvice(updateReviewDTO.getAdvice());
        existingReview.setRating(updateReviewDTO.getRating());
        existingReview.setCareerOpportunities(updateReviewDTO.getCareerOpportunities());
        existingReview.setWorkLifeBalance(updateReviewDTO.getWorkLifeBalance());
        existingReview.setCompensation(updateReviewDTO.getCompensation());
        existingReview.setJobSecurity(updateReviewDTO.getJobSecurity());
        existingReview.setManagement(updateReviewDTO.getManagement());
        existingReview.setPosition(updateReviewDTO.getPosition());
        existingReview.setEmploymentStatus(updateReviewDTO.getEmploymentStatus());
        existingReview.setEmploymentType(updateReviewDTO.getEmploymentType());
        existingReview.setRecommendToFriend(updateReviewDTO.getRecommendToFriend());
        existingReview.setAnonymous(updateReviewDTO.getAnonymous());

        // Обновляем автора в зависимости от анонимности
        if (updateReviewDTO.getAnonymous() == null || !updateReviewDTO.getAnonymous()) {
            existingReview.setAuthor(currentUser.getFullName());
        } else {
            existingReview.setAuthor(null);
        }

        // При редактировании отзыв снова должен быть проверен
        existingReview.setApprovalStatus(ApprovalStatus.PENDING);

        // Обновляем время изменения
        existingReview.setUpdatedAt(LocalDateTime.now());

        // Сохраняем обновленный отзыв
        Review updatedReview = reviewRepository.save(existingReview);

        // Преобразуем в DTO для ответа
        ReviewResponseDTO response = convertToReviewResponseDTO(updatedReview);

        // Обновляем поля связанные с договором
        response.setHasVerification(updatedReview.getContractDocumentUrl() != null &&
                !updatedReview.getContractDocumentUrl().isEmpty());
        response.setVerified(updatedReview.getContractDocumentUrl() != null &&
                !updatedReview.getContractDocumentUrl().isEmpty());

        return response;
    }
}
