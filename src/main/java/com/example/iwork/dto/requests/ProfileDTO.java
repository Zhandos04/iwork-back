package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления профиля пользователя")
public class ProfileDTO {

    @Schema(description = "ФИО пользователя", example = "Иванов Иван Иванович")
    @Pattern(
            regexp = "^[А-Яа-яЁёA-Za-z\\s-]{2,100}$",
            message = "ФИО должно содержать только русские и латинские буквы, пробелы и дефисы, от 2 до 100 символов"
    )
    private String fullName;

    @Schema(description = "Идентификатор должности из справочника", example = "42")
    @NotNull(message = "Идентификатор должности обязателен")
    private Long jobId;

    @Schema(description = "Компания пользователя", example = "ООО Рога и Копыта")
    private String company;

    @Schema(description = "Местоположение пользователя", example = "Москва, Россия")
    private String location;

    @Schema(description = "Номер телефона в формате +7XXXXXXXXXX", example = "+77011234567")
    @Pattern(
            regexp = "^$|^(\\+7)\\d{10}$",
            message = "Phone number должен соответствовать формату +7XXXXXXXXXX"
    )
    private String phone;
}
