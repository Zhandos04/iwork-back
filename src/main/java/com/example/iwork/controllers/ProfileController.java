package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.PasswordDTO;
import com.example.iwork.dto.requests.ProfileDTO;
import com.example.iwork.dto.responses.ProfileResponse;
import com.example.iwork.services.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    @Operation(
            summary = "Получение данных профиля",
            description = "Возвращает данные профиля пользователя"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно получен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content)
    })
    public ResponseEntity<Response<?>> get() {
        ProfileResponse profileResponse = profileService.getProfile();
        Response<ProfileResponse> response = new Response<>(
                profileResponse,
                "Профиль и фильтры успешно получены",
                null,
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit")
    @Operation(
            summary = "Редактирование профиля пользователя",
            description = "Обновляет профиль пользователя на основе предоставленных данных."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Профиль успешно обновлён",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Неверные данные профиля", content = @Content)
    })
    public ResponseEntity<Response<?>> edit(@RequestBody @Valid ProfileDTO profileDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        ProfileResponse profileResponse = profileService.editProfile(profileDTO);
        Response<ProfileResponse> response = new Response<>(
                profileResponse,
                "Профиль успешно обновлён",
                null,
                HttpStatus.OK.value()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-password")
    @Operation(
            summary = "Изменение пароля",
            description = "Позволяет изменить пароль пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пароль успешно изменен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "Некорректные данные", content = @Content)
    })
    public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid PasswordDTO passwordDTO) {
        profileService.updatePassword(passwordDTO);
        Response<String> response = new Response<>(null, "Пароль успешно изменен", null, HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }
}
