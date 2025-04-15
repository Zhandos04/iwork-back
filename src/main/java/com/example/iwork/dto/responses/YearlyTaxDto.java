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
@Schema(description = "Данные о налоговых выплатах за год")
public class YearlyTaxDto {
    @Schema(description = "Год", example = "2022")
    private Integer year;

    @Schema(description = "Сумма налоговых выплат", example = "45750000")
    private Double amount;

    @Schema(description = "Отформатированная сумма налоговых выплат", example = "$45.75M")
    private String formattedAmount;

    @Schema(description = "Источник данных", example = "Налоговая служба")
    private String dataSource;
}
