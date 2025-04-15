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
@Schema(description = "Исторические данные об акции")
public class HistoricalStockDataDto {
    @Schema(description = "Дата", example = "2023-04-15")
    private String date;

    @Schema(description = "Цена открытия", example = "2732.50")
    private Double open;

    @Schema(description = "Максимальная цена", example = "2756.44")
    private Double high;

    @Schema(description = "Минимальная цена", example = "2720.35")
    private Double low;

    @Schema(description = "Цена закрытия", example = "2745.38")
    private Double close;

    @Schema(description = "Объем торгов", example = "1250000")
    private Long volume;
}