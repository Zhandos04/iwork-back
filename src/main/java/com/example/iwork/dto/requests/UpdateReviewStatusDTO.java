package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Данные для обновления статуса отзыва администратором")
public class UpdateReviewStatusDTO {
    @NotNull(message = "Статус обязателен")
    @Schema(description = "Новый статус отзыва", example = "APPROVED", allowableValues = {"APPROVED", "REJECTED", "PENDING"})
    private String status;

    @Schema(description = "Комментарий администратора", example = "Отзыв соответствует правилам сервиса")
    private String adminComment;
}