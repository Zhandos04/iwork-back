package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Данные для создания или обновления отзыва")
public class CreateReviewDTO {
    @NotNull(message = "ID компании обязателен")
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @NotBlank(message = "Должность обязательна")
    @Size(max = 100, message = "Должность не должна превышать 100 символов")
    @Schema(description = "Должность пользователя в компании", example = "Разработчик ПО", maxLength = 100)
    private String position;

    @NotBlank(message = "Статус занятости обязателен")
    @Schema(description = "Статус занятости", example = "current", allowableValues = {"current", "former"})
    private String employmentStatus; // current, former

    @NotBlank(message = "Тип занятости обязателен")
    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract", "intern", "other"})
    private String employmentType; // full-time, part-time, contract, etc.

    // Рейтинги
    @NotNull(message = "Общая оценка обязательна")
    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Общая оценка компании", example = "4.5", minimum = "1", maximum = "5")
    private Double rating;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Оценка карьерных возможностей", example = "4.0", minimum = "1", maximum = "5")
    private Double careerOpportunities;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Оценка баланса работы и личной жизни", example = "3.5", minimum = "1", maximum = "5")
    private Double workLifeBalance;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Оценка компенсации и бонусов", example = "4.2", minimum = "1", maximum = "5")
    private Double compensation;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Оценка стабильности работы", example = "4.7", minimum = "1", maximum = "5")
    private Double jobSecurity;

    @Min(value = 1, message = "Оценка должна быть не менее 1")
    @Max(value = 5, message = "Оценка должна быть не более 5")
    @Schema(description = "Оценка руководства", example = "3.8", minimum = "1", maximum = "5")
    private Double management;

    @NotBlank(message = "Заголовок отзыва обязателен")
    @Size(max = 200, message = "Заголовок не должен превышать 200 символов")
    @Schema(description = "Заголовок отзыва", example = "Отличное место для начала карьеры", maxLength = 200)
    private String title;

    @NotBlank(message = "Тело отзыва обязателен")
    @Size(max = 5000, message = "Тело не должен превышать 5000 символов")
    @Schema(description = "Основной текст отзыва", example = "Я работал в этой компании 2 года...", maxLength = 5000)
    private String body;

    @NotBlank(message = "Укажите плюсы работы в компании")
    @Size(max = 5000, message = "Текст плюсов не должен превышать 5000 символов")
    @Schema(description = "Плюсы работы в компании", example = "Хороший коллектив, интересные проекты...", maxLength = 5000)
    private String pros;

    @NotBlank(message = "Укажите минусы работы в компании")
    @Size(max = 5000, message = "Текст минусов не должен превышать 5000 символов")
    @Schema(description = "Минусы работы в компании", example = "Высокая нагрузка, частые переработки...", maxLength = 5000)
    private String cons;

    @Size(max = 5000, message = "Текст советов не должен превышать 5000 символов")
    @Schema(description = "Советы руководству", example = "Стоит обратить внимание на процессы...", maxLength = 5000)
    private String advice;

    @Schema(description = "Рекомендация компании друзьям", example = "true")
    private Boolean recommendToFriend;

    @Schema(description = "Опубликовать отзыв анонимно", example = "false")
    private Boolean anonymous;

    @AssertTrue(message = "Необходимо подтвердить достоверность информации")
    @Schema(description = "Подтверждение достоверности информации", example = "true")
    private Boolean confirmTruthful;
}