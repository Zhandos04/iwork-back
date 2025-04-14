package com.example.iwork.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateSalaryDTO {
    @NotNull(message = "ID компании обязателен")
    private Long companyId;

    @NotBlank(message = "Должность обязательна")
    @Size(max = 100, message = "Должность не должна превышать 100 символов")
    private String position;

    @Size(max = 100, message = "Название отдела не должно превышать 100 символов")
    private String department;

    @NotBlank(message = "Статус занятости обязателен")
    private String employmentStatus; // current, former

    @NotBlank(message = "Тип занятости обязателен")
    private String employmentType; // full-time, part-time, contract, internship, freelance

    @NotNull(message = "Размер зарплаты обязателен")
    @Min(value = 0, message = "Зарплата не может быть отрицательной")
    private Double salary;

    @NotBlank(message = "Валюта обязательна")
    private String currency; // USD, EUR, KZT, RUB

    @NotBlank(message = "Период выплаты обязателен")
    private String payPeriod; // monthly, yearly

    @Size(max = 500, message = "Описание бонусов не должно превышать 500 символов")
    private String bonuses;

    @Size(max = 500, message = "Описание опционов не должно превышать 500 символов")
    private String stockOptions;

    private String experience; // 0-1, 1-3, 3-5, 5-10, 10+

    @Size(max = 200, message = "Местоположение не должно превышать 200 символов")
    private String location;

    private Boolean anonymous = true;

    @AssertTrue(message = "Необходимо подтвердить достоверность информации")
    private Boolean confirmTruthful;
}