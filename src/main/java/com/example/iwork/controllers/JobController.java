package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.responses.JobDTO;
import com.example.iwork.services.JobService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Tag(name = "Должности", description = "API для работы со справочником должностей")
public class JobController {
    private final JobService jobService;

    @GetMapping
    @Operation(
            summary = "Получение списка всех должностей",
            description = "Возвращает список всех должностей с возможностью фильтрации по категории или строке поиска."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список должностей успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = JobDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> getAllJobs(
            @Parameter(description = "Категория должностей для фильтрации")
            @RequestParam(required = false) String category,

            @Parameter(description = "Строка поиска в названии должности")
            @RequestParam(required = false) String search) {

        List<JobDTO> jobs = jobService.getAllJobs(category, search);

        Response<List<JobDTO>> response = new Response<>(
                jobs,
                "Список должностей успешно получен",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Получение должности по ID",
            description = "Возвращает данные конкретной должности по её идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Должность успешно получена",
                    content = @Content(schema = @Schema(implementation = JobDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Должность не найдена"
            )
    })
    public ResponseEntity<Response<?>> getJobById(
            @Parameter(description = "Идентификатор должности", required = true)
            @PathVariable Long id) {

        JobDTO job = jobService.getJobById(id);

        Response<JobDTO> response = new Response<>(
                job,
                "Должность успешно получена",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}