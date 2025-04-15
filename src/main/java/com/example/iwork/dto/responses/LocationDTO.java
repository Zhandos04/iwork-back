package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для информации о локации")
public class LocationDTO {
    @Schema(description = "Идентификатор локации")
    private Long id;

    @Schema(description = "Значение локации (город, страна)")
    private String locationValue;
}