package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Статистика по зарплатам в компании")
public class SalaryStatistics {
    @Schema(description = "Средняя зарплата по уровням опыта")
    private Map<String, Double> averageSalaryByExperience;

    @Schema(description = "Средняя зарплата для всех должностей", example = "128500")
    private Double averageSalary;

    @Schema(description = "Самая высокая зарплата", example = "250000")
    private Double highestSalary;

    @Schema(description = "Количество различных должностей", example = "25")
    private Integer totalPositions;

    @Schema(description = "Топ-5 высокооплачиваемых должностей")
    private List<TopSalaryDto> topSalaries;

    @Schema(description = "Распределение зарплат по диапазонам")
    private List<SalaryDistributionDto> salaryDistribution;
}