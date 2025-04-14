package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.CreateSalaryDTO;
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
@RequestMapping("/salary")
@RequiredArgsConstructor
@Tag(name = "Зарплаты", description = "API для управления информацией о зарплатах")
public class SalaryController {
    private final SalaryService salaryService;

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Добавление информации о зарплате с загрузкой трудового договора",
            description = "Создает новую запись о зарплате от имени текущего авторизованного пользователя. Позволяет загрузить файл трудового договора."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о зарплате успешно добавлена",
                    content = @Content(schema = @Schema(implementation = SalaryResponseDTO.class))
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
    public ResponseEntity<Response<?>> createSalary(
            @Parameter(description = "Данные о зарплате в формате JSON", required = true)
            @RequestPart(value = "salary") @Valid CreateSalaryDTO createSalaryDTO,

            @Parameter(description = "Файл трудового договора (опционально, PDF/DOCX, макс. 10MB)")
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile,

            BindingResult bindingResult) throws FileUploadException {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        // Вызов сервисного метода для создания записи о зарплате с файлом
        SalaryResponseDTO salaryResponseDTO = salaryService.addSalary(createSalaryDTO, contractFile);

        // Формирование ответа
        Response<SalaryResponseDTO> response = new Response<>(
                salaryResponseDTO,
                "Информация о зарплате успешно добавлена" + (contractFile != null && !contractFile.isEmpty() ? " с документом" : ""),
                null,
                HttpStatus.CREATED.value()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @Operation(
            summary = "Получение списка записей о зарплате текущего пользователя",
            description = "Возвращает список записей о зарплате, созданных текущим авторизованным пользователем"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Список записей о зарплате успешно получен",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SalaryResponseDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Пользователь не авторизован"
            )
    })
    public ResponseEntity<Response<?>> getMySalaries(
            @Parameter(description = "Фильтр по статусу (Новый, Одобрено, Отказано, Все)")
            @RequestParam(required = false) String status) {

        List<SalaryResponseDTO> salaries = salaryService.getMySalaries(status);

        Response<List<SalaryResponseDTO>> response = new Response<>(
                salaries,
                "Информация о зарплатах успешно получена",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление записи о зарплате",
            description = "Удаляет запись о зарплате по указанному идентификатору. Пользователь может удалять только свои записи."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запись о зарплате успешно удалена"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет прав на удаление записи"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись о зарплате не найдена"
            )
    })
    public ResponseEntity<Response<?>> deleteSalary(
            @Parameter(description = "Идентификатор записи о зарплате", required = true)
            @PathVariable Long id) throws FileUploadException {

        salaryService.deleteSalary(id);

        Response<Void> response = new Response<>(
                null,
                "Информация о зарплате успешно удалена",
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Обновление записи о зарплате",
            description = "Обновляет информацию о зарплате. Пользователь может редактировать только свои записи. Также позволяет обновить файл трудового договора."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Запись о зарплате успешно обновлена",
                    content = @Content(schema = @Schema(implementation = SalaryResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации данных или проблема с загрузкой файла"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Нет прав на редактирование записи"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Запись о зарплате не найдена"
            )
    })
    public ResponseEntity<Response<?>> updateSalary(
            @Parameter(description = "Идентификатор записи о зарплате", required = true)
            @PathVariable Long id,

            @Parameter(description = "Данные о зарплате в формате JSON", required = true)
            @RequestPart(value = "salary") @Valid CreateSalaryDTO updateSalaryDTO,

            @Parameter(description = "Файл трудового договора (опционально, PDF/DOCX, макс. 10MB)")
            @RequestPart(value = "contractFile", required = false) MultipartFile contractFile,

            BindingResult bindingResult) throws FileUploadException {

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        // Вызов сервисного метода для обновления записи о зарплате с файлом
        SalaryResponseDTO updatedSalary = salaryService.updateSalary(id, updateSalaryDTO, contractFile);

        // Формирование ответа с учетом наличия загруженного файла
        String successMessage = "Информация о зарплате успешно обновлена";
        if (contractFile != null && !contractFile.isEmpty()) {
            successMessage += " с обновленным документом";
        }

        Response<SalaryResponseDTO> response = new Response<>(
                updatedSalary,
                successMessage,
                null,
                HttpStatus.OK.value()
        );

        return ResponseEntity.ok(response);
    }
}