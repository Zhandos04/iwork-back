package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.UpdateSalaryStatusDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;
import com.example.iwork.services.SalaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/salaries")
@RequiredArgsConstructor
@Tag(name = "Администрирование зарплат", description = "API для администрирования информации о зарплатах")
public class AdminSalaryController {
    private final SalaryService salaryService;

    @GetMapping
    @Operation(
            summary = "Получение списка всех записей о зарплатах",
            description = "Возвращает список всех записей о зарплатах с возможностью фильтрации по статусу. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список записей о зарплатах успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = SalaryResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> getAllSalaries(
            @Parameter(description = "Фильтр по статусу (Новый, Одобрено, Отказано, Все)")
            @RequestParam(required = false) String status) {

        List<SalaryResponseDTO> salaries = salaryService.getAllSalaries(status);

        Response<List<SalaryResponseDTO>> response = new Response<>(
                salaries,
                "Информация о зарплатах успешно получена",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Обновление статуса записи о зарплате",
            description = "Обновляет статус записи о зарплате (одобрение/отклонение) и добавляет комментарий администратора. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус записи о зарплате успешно обновлен",
                    content = @Content(schema = @Schema(implementation = SalaryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные запроса"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись о зарплате не найдена"
            )
    })
    public ResponseEntity<Response<?>> updateSalaryStatus(
            @Parameter(description = "Идентификатор записи о зарплате", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateSalaryStatusDTO.class))
            )
            @RequestBody @Valid UpdateSalaryStatusDTO updateStatusDTO) {

        SalaryResponseDTO updatedSalary = salaryService.updateSalaryStatus(id, updateStatusDTO);

        String message = switch (updateStatusDTO.getStatus()) {
            case "APPROVED" -> "Запись о зарплате успешно одобрена";
            case "REJECTED" -> "Запись о зарплате отклонена";
            default -> "Статус записи о зарплате успешно обновлен";
        };

        if (updateStatusDTO.getAdminComment() != null && !updateStatusDTO.getAdminComment().trim().isEmpty()) {
            message += " с комментарием";
        }

        Response<SalaryResponseDTO> response = new Response<>(
                updatedSalary,
                message,
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}