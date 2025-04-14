package com.example.iwork.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateReviewDTO {
    @NotNull(message = "ID компании обязателен")
    private Long companyId;

    @NotBlank(message = "Должность обязательна")
    @Size(max = 100, message = "Должность не должна превышать 100 символов")
    private String position;

    @NotBlank(message = "Статус занятости обязателен")
    private String employmentStatus; // current, former

    @NotBlank(message = "Тип занятости обязателен")
    private String employmentType; // full-time, part-time, contract, etc.

    private String contractDocumentUrl;

    // Рейтинги
    @NotNull(message = "Общая оценка обязательна")
    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double rating;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double careerOpportunities;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double workLifeBalance;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double compensation;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double jobSecurity;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    private Double management;

    @NotBlank(message = "Заголовок отзыва обязателен")
    @Size(max = 200, message = "Заголовок не должен превышать 200 символов")
    private String title;

    @NotBlank(message = "Тело отзыва обязателен")
    @Size(max = 5000, message = "Тело не должен превышать 5000 символов")
    private String body;

    @NotBlank(message = "Укажите плюсы работы в компании")
    @Size(max = 5000, message = "Текст плюсов не должен превышать 5000 символов")
    private String pros;

    @NotBlank(message = "Укажите минусы работы в компании")
    @Size(max = 5000, message = "Текст минусов не должен превышать 5000 символов")
    private String cons;

    @Size(max = 5000, message = "Текст советов не должен превышать 5000 символов")
    private String advice;

    private Boolean recommendToFriend;

    private Boolean anonymous;

    @AssertTrue(message = "Необходимо подтвердить достоверность информации")
    private Boolean confirmTruthful;
}