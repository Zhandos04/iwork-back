package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Данные о компании")
public class CompanyResponseDto {
    @Schema(description = "Идентификатор компании", example = "1")
    private Long id;

    @Schema(description = "Название компании", example = "Google")
    private String name;

    @Schema(description = "URL логотипа компании", example = "https://example.com/logos/google.png")
    private String logoUrl;

    @Schema(description = "Краткое описание компании", example = "Технологическая компания, специализирующаяся на интернет-сервисах")
    private String description;

    @Schema(description = "Местоположение главного офиса компании", example = "Маунтин-Вью, Калифорния")
    private String location;

    @Schema(description = "Рейтинг компании", example = "4.5")
    private Double rating;

    @Schema(description = "Размер компании", example = "large", allowableValues = {"small", "medium", "large"})
    private String size;

    @Schema(description = "Список отраслей компании")
    private List<String> industries;
}
