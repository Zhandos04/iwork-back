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
@Schema(description = "Обзорная информация о компании")
public class CompanyOverviewResponseDto {
    // Основная информация о компании
    @Schema(description = "Идентификатор компании", example = "1")
    private Long id;

    @Schema(description = "Название компании", example = "Google")
    private String name;

    @Schema(description = "URL логотипа компании", example = "https://example.com/logos/google.png")
    private String logoUrl;

    @Schema(description = "URL баннера компании", example = "https://example.com/banners/google.png")
    private String bannerImg;

    @Schema(description = "Полное описание компании", example = "Google — американская транснациональная технологическая компания, специализирующаяся на интернет-сервисах и продуктах.")
    private String description;

    @Schema(description = "Местоположение главного офиса компании", example = "Маунтин-Вью, Калифорния")
    private String location;

    @Schema(description = "Рейтинг компании", example = "4.5")
    private Double rating;

    @Schema(description = "Размер компании", example = "large", allowableValues = {"small", "medium", "large"})
    private String size;

    @Schema(description = "Список отраслей компании")
    private List<String> industries;

    // Общая информация
    @Schema(description = "Год основания компании", example = "1998")
    private String founded;

    @Schema(description = "Годовой доход компании", example = "$182.5 billion")
    private String revenue;

    @Schema(description = "Миссия компании", example = "Организовать мировую информацию и сделать ее доступной и полезной для всех")
    private String mission;

    // Один отзыв
    @Schema(description = "Лучший отзыв о компании")
    private ReviewSummaryDto topReview;

    // Количество всех отзывов
    @Schema(description = "Общее количество отзывов о компании", example = "2345")
    private Integer totalReviews;

    // Одна зарплата
    @Schema(description = "Лучшие данные о зарплате в компании")
    private SalarySummaryDto topSalary;

    // Количество всех зарплат
    @Schema(description = "Общее количество данных о зарплатах в компании", example = "1250")
    private Integer totalSalaries;
}
