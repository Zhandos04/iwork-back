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
@Schema(description = "Информация о высокооплачиваемой должности")
public class TopSalaryDto {
    @Schema(description = "Название должности", example = "Chief Technology Officer")
    private String position;

    @Schema(description = "Медианная зарплата", example = "225000")
    private Double median;
}