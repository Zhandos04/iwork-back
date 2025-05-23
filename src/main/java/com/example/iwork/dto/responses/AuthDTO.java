package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO с данными после авторизации")
public class AuthDTO {

    @Schema(description = "JWT-токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "JWT-токен для обновления", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "ФИО", example = "Иван Иванов")
    private String fullName;

    @Schema(description = "Должность пользователя")
    private String jobTitle;

    @Schema(description = "Компания пользователя")
    private String company;

    @Schema(description = "Местоположение пользователя")
    private String location;

    @Schema(description = "Email пользователя", example = "example@example.com")
    private String email;

    @Schema(description = "Номер телефона пользователя", example = "+77011234567")
    private String phone;

    @Schema(description = "С нами с", example = "Май 2022")
    private String withUsSince;

    @Schema(description = "Роль пользователя")
    private String role;
}