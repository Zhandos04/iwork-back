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
@Schema(description = "Данные об отзывах о компании")
public class CompanyReviewsResponseDto {
    // Общая информация
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @Schema(description = "Название компании", example = "Google")
    private String companyName;

    @Schema(description = "Средний рейтинг компании", example = "4.5")
    private Double averageRating;

    @Schema(description = "Общее количество отзывов", example = "2345")
    private Integer totalReviews;

    // Статистика по рейтингам
    @Schema(description = "Распределение рейтингов (ключ - рейтинг от 1 до 5, значение - количество отзывов)")
    private Map<Integer, Integer> ratingDistribution;

    // Список отзывов для текущей страницы
    @Schema(description = "Список отзывов на текущей странице")
    private List<ReviewDto> reviews;

    // Информация для пагинации
    @Schema(description = "Текущая страница", example = "0")
    private Integer currentPage;

    @Schema(description = "Общее количество страниц", example = "47")
    private Integer totalPages;

    @Schema(description = "Размер страницы", example = "5")
    private Integer pageSize;
}
