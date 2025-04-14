package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.CreateReviewDTO;
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
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Отзывы", description = "API для управления отзывами пользователей о компаниях")
public class ReviewController {
    private final ReviewService reviewService;
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Создание нового отзыва с загрузкой трудового договора",
            description = "Создает новый отзыв о компании от имени текущего авторизованного пользователя. Позволяет одновременно загрузить файл трудового договора."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Отзыв успешно создан",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных или проблема с загрузкой файла"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> createReview(
            @Parameter(description = "Данные отзыва в формате JSON", required = true)
            @RequestPart(value = "review") @Valid CreateReviewDTO createReviewDTO,

            @Parameter(description = "Файл трудового договора (опционально, PDF/DOCX, макс. 10MB)")
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile,

            BindingResult bindingResult) throws FileUploadException {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        ReviewResponseDTO reviewResponseDTO = reviewService.addReview(createReviewDTO, contractFile);

        Response<ReviewResponseDTO> response = new Response<>(
                reviewResponseDTO,
                "Отзыв успешно создан" + (contractFile != null && !contractFile.isEmpty() ? " с документом" : ""),
                null,
                HttpStatus.CREATED.value()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Получение списка отзывов текущего пользователя",
            description = "Возвращает список отзывов, созданных текущим авторизованным пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список отзывов успешно получен",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReviewResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> getMyReviews(
            @Parameter(description = "Фильтр по статусу отзыва (Новый, Одобрено, Отказано, Все)")
            @RequestParam(required = false) String status) {

        List<ReviewResponseDTO> reviews = reviewService.getMyReviews(status);

        Response<List<ReviewResponseDTO>> response = new Response<>(
                reviews,
                "Отзывы успешно получены",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление отзыва",
            description = "Удаляет отзыв по указанному идентификатору. Пользователь может удалять только свои отзывы."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Отзыв успешно удален"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет прав на удаление отзыва"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Отзыв не найден"
            )
    })
    public ResponseEntity<Response<?>> deleteReview(
            @Parameter(description = "Идентификатор отзыва", required = true)
            @PathVariable Long id) {

        reviewService.deleteReview(id);

        Response<Void> response = new Response<>(
                null,
                "Отзыв успешно удален",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление существующего отзыва",
            description = "Обновляет информацию в существующем отзыве и при необходимости загружает новый файл трудового договора. Пользователь может редактировать только свои отзывы."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Отзыв успешно обновлен",
                    content = @Content(schema = @Schema(implementation = ReviewResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных или проблема с загрузкой файла"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет прав на редактирование отзыва"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Отзыв не найден"
            )
    })
    public ResponseEntity<Response<?>> updateReview(
            @Parameter(description = "Идентификатор отзыва", required = true)
            @PathVariable Long id,

            @Parameter(description = "Данные отзыва в формате JSON", required = true)
            @RequestPart(value = "review") @Valid CreateReviewDTO updateReviewDTO,

            @Parameter(description = "Файл трудового договора (опционально, PDF/DOCX, макс. 10MB)")
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile,

            BindingResult bindingResult) throws FileUploadException {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        ReviewResponseDTO updatedReview = reviewService.updateReview(id, updateReviewDTO, contractFile);

        String successMessage = "Отзыв успешно обновлен";
        if (contractFile != null && !contractFile.isEmpty()) {
            successMessage += " с обновленным документом";
        }

        Response<ReviewResponseDTO> response = new Response<>(
                updatedReview,
                successMessage,
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}