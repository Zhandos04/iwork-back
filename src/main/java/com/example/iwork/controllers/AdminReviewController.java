package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.UpdateReviewStatusDTO;
import com.example.iwork.dto.responses.ReviewResponseDTO;
import com.example.iwork.services.ReviewService;
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
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
@Tag(name = "Администрирование отзывов", description = "API для администрирования отзывов")
public class AdminReviewController {
    private final ReviewService reviewService;

    @GetMapping
    @Operation(
            summary = "Получение списка всех отзывов",
            description = "Возвращает список всех отзывов с возможностью фильтрации по статусу. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список отзывов успешно получен",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))
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
    public ResponseEntity<Response<?>> getAllReviews(
            @Parameter(description = "Фильтр по статусу отзыва (Новый, Одобрено, Отказано, Все)")
            @RequestParam(required = false) String status) {

        List<ReviewResponseDTO> reviews = reviewService.getAllReviews(status);

        Response<List<ReviewResponseDTO>> response = new Response<>(
                reviews,
                "Отзывы успешно получены",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @Operation(
            summary = "Обновление статуса отзыва",
            description = "Обновляет статус отзыва (одобрение/отклонение) и добавляет комментарий администратора. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Статус отзыва успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))
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
                    description = "Отзыв не найден"
            )
    })
    public ResponseEntity<Response<?>> updateReviewStatus(
            @Parameter(description = "Идентификатор отзыва", required = true)
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления статуса",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateReviewStatusDTO.class))
            )
            @RequestBody @Valid UpdateReviewStatusDTO updateStatusDTO) {

        ReviewResponseDTO updatedReview = reviewService.updateReviewStatus(id, updateStatusDTO);

        String message = switch (updateStatusDTO.getStatus()) {
            case "APPROVED" -> "Отзыв успешно одобрен";
            case "REJECTED" -> "Отзыв отклонен";
            default -> "Статус отзыва успешно обновлен";
        };

        if (updateStatusDTO.getAdminComment() != null && !updateStatusDTO.getAdminComment().trim().isEmpty()) {
            message += " с комментарием";
        }

        Response<ReviewResponseDTO> response = new Response<>(
                updatedReview,
                message,
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}