package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Налоговая информация о компании")
public class CompanyTaxesResponseDto {
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @Schema(description = "Название компании", example = "Google")
    private String companyName;

    @Schema(description = "Дата регистрации компании", example = "1998-09-04")
    private LocalDate registrationDate;

    @Schema(description = "Статус компании", example = "Активна")
    private String companyStatus;

    @Schema(description = "Тип компании", example = "Публичная компания")
    private String companyType;

    @Schema(description = "Размер компании", example = "Крупная")
    private String companySize;

    @Schema(description = "Сфера деятельности", example = "Информационные технологии")
    private String businessActivity;

    @Schema(description = "Код деятельности", example = "62.01")
    private String businessActivityCode;

    @Schema(description = "Дата последнего обновления данных", example = "2023-04-15")
    private LocalDate lastUpdateDate;

    @Schema(description = "Источник данных", example = "Налоговая служба")
    private String dataSource;

    @Schema(description = "Является ли плательщиком НДС", example = "true")
    private Boolean vatPayer;

    @Schema(description = "Является ли участником Астана Хаб", example = "false")
    private Boolean astanaHubParticipant;

    @Schema(description = "Участвует ли в госзакупках", example = "true")
    private Boolean governmentProcurementParticipant;

    @Schema(description = "Количество лицензий", example = "8")
    private Integer licenseCount;

    @Schema(description = "Дата последнего изменения документов", example = "2023-01-15")
    private LocalDate lastDocumentChangeDate;

    @Schema(description = "Количество участий в других компаниях", example = "5")
    private Integer participationsInOtherCompanies;

    @Schema(description = "Ежегодные налоговые данные")
    private List<YearlyTaxDto> yearlyTaxes;

    @Schema(description = "Годовой доход", example = "182500000000")
    private Double annualRevenue;

    @Schema(description = "Отформатированный годовой доход", example = "$182.5B")
    private String annualRevenueFormatted;
}