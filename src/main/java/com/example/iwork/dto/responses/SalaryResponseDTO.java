package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Информация о зарплате для отображения")
public class SalaryResponseDTO {
    @Schema(description = "Идентификатор записи о зарплате", example = "123")
    private Long id;

    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @Schema(description = "Название компании", example = "ООО Технологии")
    private String companyName;

    @Schema(description = "Должность в компании", example = "Разработчик ПО")
    private String position;

    @Schema(description = "Название отдела или подразделения", example = "Отдел разработки")
    private String department;

    @Schema(description = "Статус занятости", example = "current", allowableValues = {"current", "former"})
    private String employmentStatus;

    @Schema(description = "Тип занятости", example = "full-time", allowableValues = {"full-time", "part-time", "contract", "internship", "freelance"})
    private String employmentType;

    @Schema(description = "Размер зарплаты в числовом формате", example = "120000.00")
    private Double salary;

    @Schema(description = "Валюта", example = "RUB", allowableValues = {"USD", "EUR", "KZT", "RUB"})
    private String currency;

    @Schema(description = "Период выплаты", example = "monthly", allowableValues = {"monthly", "yearly"})
    private String payPeriod;

    @Schema(description = "Отформатированная сумма с валютой", example = "₽120000.00 в месяц")
    private String formattedAmount;

    @Schema(description = "Информация о бонусах и премиях", example = "Квартальная премия до 20% от оклада")
    private String bonuses;

    @Schema(description = "Информация о опционах на акции", example = "Опционы на акции после года работы")
    private String stockOptions;

    @Schema(description = "Опыт работы", example = "3-5", allowableValues = {"0-1", "1-3", "3-5", "5-10", "10+"})
    private String experience;

    @Schema(description = "Местоположение офиса или город работы", example = "Москва")
    private String location;

    @Schema(description = "Информация опубликована анонимно", example = "true")
    private Boolean anonymous;

    @Schema(description = "Статус утверждения", example = "PENDING", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private String approvalStatus;

    @Schema(description = "Наличие верификации (подтверждающего документа)", example = "true")
    private Boolean hasVerification;

    @Schema(description = "URL документа, подтверждающего работу в компании", example = "https://storage.example.com/documents/contract123.pdf")
    private String contractDocumentUrl;

    @Schema(description = "Дата создания записи в формате 'Месяц Год' на русском", example = "Апрель 2023")
    private String date;
}