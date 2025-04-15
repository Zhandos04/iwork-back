package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Краткая информация о зарплате")
public class SalarySummaryDto {
    @Schema(description = "Идентификатор записи о зарплате", example = "456")
    private Long id;

    @Schema(description = "Название должности", example = "Senior Software Engineer")
    private String position;

    @Schema(description = "Медианное значение зарплаты", example = "145000")
    private Double median;

    @Schema(description = "Минимальное значение зарплаты", example = "130000")
    private Double min;

    @Schema(description = "Максимальное значение зарплаты", example = "165000")
    private Double max;

    @Schema(description = "Валюта", example = "USD")
    private String currency;

    @Schema(description = "Уровень опыта", example = "senior", allowableValues = {"entry", "mid", "senior", "executive"})
    private String experienceLevel;

    @Schema(description = "Дополнительные выплаты", example = "15000")
    private Double additionalPay;
}