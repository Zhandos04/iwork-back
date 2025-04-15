package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Данные для обновления статуса записи о зарплате администратором")
public class UpdateSalaryStatusDTO {
    @NotNull(message = "Статус обязателен")
    @Schema(description = "Новый статус записи о зарплате", example = "APPROVED", allowableValues = {"APPROVED", "REJECTED", "PENDING"})
    private String status;

    @Schema(description = "Комментарий администратора", example = "Информация о зарплате соответствует рыночным показателям")
    private String adminComment;
}