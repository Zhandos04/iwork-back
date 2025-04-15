package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Данные о должности")
public class JobDTO {

    @Schema(description = "Идентификатор должности", example = "42")
    private Long id;

    @Schema(description = "Название должности", example = "Старший разработчик Java")
    private String title;

    @Schema(description = "Описание должности", example = "Разработка и поддержка серверной части приложений на Java")
    private String description;

    @Schema(description = "Категория должности", example = "IT")
    private String category;
}