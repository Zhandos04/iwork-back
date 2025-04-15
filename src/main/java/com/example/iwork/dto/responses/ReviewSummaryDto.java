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
@Schema(description = "Краткая информация об отзыве")
public class ReviewSummaryDto {
    @Schema(description = "Идентификатор отзыва", example = "123")
    private Long id;

    @Schema(description = "Заголовок отзыва", example = "Отличная компания для работы")
    private String title;

    @Schema(description = "Текст отзыва (фрагмент)", example = "Компания предоставляет прекрасные возможности для роста...")
    private String body;

    @Schema(description = "Рейтинг", example = "4.5")
    private Double rating;

    @Schema(description = "Автор отзыва", example = "Анонимный сотрудник")
    private String author;

    @Schema(description = "Дата публикации", example = "2023-04-15")
    private String date;
}