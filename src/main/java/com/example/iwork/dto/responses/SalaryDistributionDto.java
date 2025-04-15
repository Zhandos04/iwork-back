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
@Schema(description = "Распределение зарплат по диапазонам")
public class SalaryDistributionDto {
    @Schema(description = "Диапазон зарплаты (округлено до тысячи)", example = "120000")
    private Double salaryRange;

    @Schema(description = "Количество должностей в этом диапазоне", example = "45")
    private Integer count;
}
