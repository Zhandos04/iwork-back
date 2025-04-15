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
@Schema(description = "Подробная информация об отзыве")
public class ReviewDto {
    @Schema(description = "Идентификатор отзыва", example = "123")
    private Long id;

    @Schema(description = "Заголовок отзыва", example = "Отличная компания для работы")
    private String title;

    @Schema(description = "Основной текст отзыва", example = "Компания предоставляет прекрасные возможности для роста...")
    private String body;

    @Schema(description = "Плюсы работы в компании", example = "Гибкий график, конкурентная зарплата")
    private String pros;

    @Schema(description = "Минусы работы в компании", example = "Иногда бывает сложно добиться баланса между работой и личной жизнью")
    private String cons;

    @Schema(description = "Совет руководству", example = "Продолжайте инвестировать в развитие сотрудников")
    private String advice;

    @Schema(description = "Общий рейтинг", example = "4.5")
    private Double rating;

    @Schema(description = "Оценка возможностей карьерного роста", example = "4.2")
    private Double careerOpportunities;

    @Schema(description = "Оценка баланса работы и личной жизни", example = "3.8")
    private Double workLifeBalance;

    @Schema(description = "Оценка компенсации и льгот", example = "4.7")
    private Double compensation;

    @Schema(description = "Оценка стабильности работы", example = "4.6")
    private Double jobSecurity;

    @Schema(description = "Оценка менеджмента", example = "4.0")
    private Double management;

    @Schema(description = "Должность автора отзыва", example = "Software Engineer")
    private String position;

    @Schema(description = "Статус трудоустройства", example = "current", allowableValues = {"current", "former"})
    private String employmentStatus;

    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract", "intern"})
    private String employmentType;

    @Schema(description = "Рекомендовал бы компанию другу", example = "true")
    private Boolean recommendToFriend;

    @Schema(description = "Автор отзыва", example = "Анонимный сотрудник")
    private String author;

    @Schema(description = "Количество голосов 'полезно'", example = "15")
    private Integer helpfulCount;

    @Schema(description = "Количество голосов 'не полезно'", example = "3")
    private Integer notHelpfulCount;

    @Schema(description = "Количество комментариев", example = "5")
    private Integer commentsCount;

    @Schema(description = "Дата публикации отзыва", example = "2023-04-15")
    private String date;
}
