package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация об акциях компании")
public class CompanyStocksResponseDto {
    @Schema(description = "Идентификатор компании", example = "1")
    private Long companyId;

    @Schema(description = "Название компании", example = "Google")
    private String companyName;

    @Schema(description = "Символ акции на бирже", example = "GOOGL")
    private String symbol;

    @Schema(description = "Текущая цена акции", example = "2745.38")
    private Double currentPrice;

    @Schema(description = "Цена закрытия предыдущего дня", example = "2730.21")
    private Double previousClose;

    @Schema(description = "Цена открытия", example = "2732.50")
    private Double open;

    @Schema(description = "Максимальная цена за день", example = "2756.44")
    private Double dayHigh;

    @Schema(description = "Минимальная цена за день", example = "2720.35")
    private Double dayLow;

    @Schema(description = "Объем торгов", example = "1250000")
    private Long volume;

    @Schema(description = "Рыночная капитализация", example = "1820000000000")
    private Long marketCap;

    @Schema(description = "Соотношение цены к прибыли", example = "27.5")
    private Double peRatio;

    @Schema(description = "Дивидендная доходность", example = "0.5")
    private Double dividendYield;

    @Schema(description = "52-недельный максимум", example = "2936.41")
    private Double fiftyTwoWeekHigh;

    @Schema(description = "52-недельный минимум", example = "2200.15")
    private Double fiftyTwoWeekLow;

    @Schema(description = "Валюта", example = "USD")
    private String currency;

    @Schema(description = "Изменение цены", example = "15.17")
    private Double priceChange;

    @Schema(description = "Процентное изменение цены", example = "0.56")
    private Double priceChangePercent;

    @Schema(description = "Отформатированная цена акции", example = "$2,745.38")
    private String formattedPrice;

    @Schema(description = "Отформатированная рыночная капитализация", example = "$1.82T")
    private String formattedMarketCap;

    @Schema(description = "Временная метка последнего обновления", example = "2023-05-12T15:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Исторические данные о цене акции")
    private List<HistoricalStockDataDto> historicalData;
}