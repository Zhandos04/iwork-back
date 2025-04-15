package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.responses.LocationDTO;
import com.example.iwork.services.LocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
@Tag(name = "Локации", description = "API для работы с локациями")
public class LocationController {
    private final LocationService locationService;

    @GetMapping
    @Operation(
            summary = "Получение списка локаций",
            description = "Возвращает список всех доступных локаций с возможностью поиска по тексту."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список локаций успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LocationDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> getLocations(
            @Parameter(description = "Строка поиска для фильтрации локаций")
            @RequestParam(required = false) String search) {

        List<LocationDTO> locations = locationService.searchLocations(search);

        Response<List<LocationDTO>> response = new Response<>(
                locations,
                "Список локаций успешно получен",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}