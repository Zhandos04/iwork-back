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
@Schema(description = "Данные о зарплате для конкретной должности")
public class SalaryDto {
    @Schema(description = "Идентификатор записи о зарплате", example = "456")
    private Long id;

    @Schema(description = "Название должности", example = "Senior Software Engineer")
    private String position;

    @Schema(description = "Отдел", example = "Engineering")
    private String department;

    @Schema(description = "Уровень опыта", example = "senior", allowableValues = {"entry", "mid", "senior", "executive"})
    private String experienceLevel;

    @Schema(description = "Зарплата", example = "150000")
    private Double salary;

    @Schema(description = "Валюта", example = "USD")
    private String currency;

    @Schema(description = "Период выплаты", example = "yearly", allowableValues = {"yearly", "monthly", "weekly", "hourly"})
    private String payPeriod;

    @Schema(description = "Медианное значение зарплаты для данной должности", example = "145000")
    private Double median;

    @Schema(description = "Минимальное значение зарплаты для данной должности", example = "130000")
    private Double min;

    @Schema(description = "Максимальное значение зарплаты для данной должности", example = "165000")
    private Double max;

    @Schema(description = "Дополнительные выплаты (бонусы, премии и т.д.)", example = "15000")
    private Double additionalPay;

    @Schema(description = "Местоположение", example = "Сан-Франциско, Калифорния")
    private String location;

    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract"})
    private String employmentType;
}