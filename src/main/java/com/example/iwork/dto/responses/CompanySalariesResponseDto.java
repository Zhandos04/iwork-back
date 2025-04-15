package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные о зарплатах в компании")
public class CompanySalariesResponseDto {
    // Общая информация
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @Schema(description = "Название компании", example = "Google")
    private String companyName;

    // Статистика
    @Schema(description = "Статистика по зарплатам")
    private SalaryStatistics statistics;

    // Список зарплат для текущей страницы
    @Schema(description = "Список данных о зарплатах на текущей странице")
    private List<SalaryDto> salaries;

    // Информация для пагинации
    @Schema(description = "Текущая страница", example = "0")
    private Integer currentPage;

    @Schema(description = "Общее количество страниц", example = "25")
    private Integer totalPages;

    @Schema(description = "Размер страницы", example = "5")
    private Integer pageSize;

    @Schema(description = "Общее количество записей о зарплатах", example = "125")
    private Integer totalSalaries;
}
