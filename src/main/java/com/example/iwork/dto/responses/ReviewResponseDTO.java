package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
@Data
@Schema(description = "Данные отзыва для отображения")
public class ReviewResponseDTO {
    @Schema(description = "Идентификатор отзыва", example = "123")
    private Long id;

    @Schema(description = "Название компании", example = "ООО Технологии")
    private String companyName;

    @Schema(description = "Заголовок отзыва", example = "Отличное место для начала карьеры")
    private String title;

    @Schema(description = "Основной текст отзыва", example = "Я работал в этой компании 2 года...")
    private String body;

    @Schema(description = "Плюсы работы в компании", example = "Хороший коллектив, интересные проекты...")
    private String pros;

    @Schema(description = "Минусы работы в компании", example = "Высокая нагрузка, частые переработки...")
    private String cons;

    @Schema(description = "Советы руководству", example = "Стоит обратить внимание на процессы...")
    private String advice;

    @Schema(description = "Общая оценка компании", example = "4.5")
    private Double rating;

    @Schema(description = "Оценка карьерных возможностей", example = "4.0")
    private Double careerOpportunities;

    @Schema(description = "Оценка баланса работы и личной жизни", example = "3.5")
    private Double workLifeBalance;

    @Schema(description = "Оценка компенсации и бонусов", example = "4.2")
    private Double compensation;

    @Schema(description = "Оценка стабильности работы", example = "4.7")
    private Double jobSecurity;

    @Schema(description = "Оценка руководства", example = "3.8")
    private Double management;

    @Schema(description = "Должность в компании", example = "Разработчик ПО")
    private String position;

    @Schema(description = "Статус занятости", example = "current", allowableValues = {"current", "former"})
    private String employmentStatus;

    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract", "intern", "other"})
    private String employmentType;

    @Schema(description = "Рекомендация компании друзьям", example = "true")
    private Boolean recommendToFriend;

    @Schema(description = "Отзыв опубликован анонимно", example = "false")
    private Boolean anonymous;

    @Schema(description = "Количество пользователей, отметивших отзыв как полезный", example = "15")
    private Integer helpfulCount;

    @Schema(description = "Количество пользователей, отметивших отзыв как бесполезный", example = "3")
    private Integer notHelpfulCount;

    @Schema(description = "Количество комментариев к отзыву", example = "7")
    private Integer commentsCount;

    @Schema(description = "Имя автора отзыва (зависит от флага anonymous)", example = "Иван Иванов")
    private String author;

    @Schema(description = "Статус утверждения отзыва", example = "APPROVED", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private String approvalStatus;

    @Schema(description = "Наличие верификации (подтверждающего документа)", example = "true")
    private Boolean hasVerification;

    @Schema(description = "URL документа, подтверждающего работу в компании", example = "https://storage.example.com/documents/contract123.pdf")
    private String contractDocumentUrl;

    @Schema(description = "Дата создания отзыва в формате 'Месяц Год' на русском", example = "Апрель 2023")
    private String date;

    @Schema(description = "Статус отзыва на русском языке", example = "Одобрено", allowableValues = {"Новый", "Одобрено", "Отказано"})
    private String status;

    @Schema(description = "Наличие комментария администратора", example = "false")
    private Boolean hasAdminComment;

    @Schema(description = "Верифицирован ли отзыв (наличие подтверждающего документа)", example = "true")
    private Boolean verified;

    @Schema(description = "Комментарий администратора", example = "Отзыв был проверен и подтвержден.")
    private String adminComment;

    @Schema(description = "Анализ отзыва от AI")
    private String aiAnalysis;
}
