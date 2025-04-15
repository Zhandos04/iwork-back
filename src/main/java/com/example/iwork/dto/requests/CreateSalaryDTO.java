package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Данные для создания или обновления записи о зарплате")
public class CreateSalaryDTO {
    @NotNull(message = "ID компании обязателен")
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @NotNull(message = "ID должности обязателен")
    @Schema(description = "Идентификатор должности из справочника", example = "42")
    private Long jobId;

    @Schema(description = "Идентификатор локации", example = "1")
    private Long locationId;

    @NotBlank(message = "Должность обязательна")
    @Size(max = 100, message = "Должность не должна превышать 100 символов")
    @Schema(description = "Должность пользователя в компании", example = "Разработчик ПО", maxLength = 100)
    private String position;

    @Size(max = 100, message = "Название отдела не должно превышать 100 символов")
    @Schema(description = "Название отдела или подразделения", example = "Отдел разработки", maxLength = 100)
    private String department;

    @NotBlank(message = "Статус занятости обязателен")
    @Schema(description = "Статус занятости", example = "current", allowableValues = {"current", "former"})
    private String employmentStatus; // current, former

    @NotBlank(message = "Тип занятости обязателен")
    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract", "internship", "freelance"})
    private String employmentType; // full-time, part-time, contract, internship, freelance

    @NotNull(message = "Размер зарплаты обязателен")
    @Min(value = 0, message = "Зарплата не может быть отрицательной")
    @Schema(description = "Размер зарплаты", example = "120000.00", minimum = "0")
    private Double salary;

    @NotBlank(message = "Валюта обязательна")
    @Schema(description = "Валюта", example = "RUB", allowableValues = {"USD", "EUR", "KZT", "RUB"})
    private String currency; // USD, EUR, KZT, RUB

    @NotBlank(message = "Период выплаты обязателен")
    @Schema(description = "Период выплаты", example = "monthly", allowableValues = {"monthly", "yearly"})
    private String payPeriod; // monthly, yearly

    @Size(max = 500, message = "Описание бонусов не должно превышать 500 символов")
    @Schema(description = "Информация о бонусах и премиях", example = "Квартальная премия до 20% от оклада", maxLength = 500)
    private String bonuses;

    @Size(max = 500, message = "Описание опционов не должно превышать 500 символов")
    @Schema(description = "Информация о опционах на акции", example = "Опционы на акции после года работы", maxLength = 500)
    private String stockOptions;

    @Schema(description = "Опыт работы", example = "3-5", allowableValues = {"0-1", "1-3", "3-5", "5-10", "10+"})
    private String experience; // 0-1, 1-3, 3-5, 5-10, 10+

    @Size(max = 200, message = "Местоположение не должно превышать 200 символов")
    @Schema(description = "Местоположение офиса или город работы", example = "Москва", maxLength = 200)
    private String location;

    @Schema(description = "Публиковать информацию анонимно", example = "true", defaultValue = "true")
    private Boolean anonymous = true;

    @Schema(description = "URL документа, подтверждающего работу в компании", example = "https://storage.example.com/documents/contract123.pdf")
    private String contractDocumentUrl;

    @AssertTrue(message = "Необходимо подтвердить достоверность информации")
    @Schema(description = "Подтверждение достоверности информации", example = "true")
    private Boolean confirmTruthful;
}