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

    @Schema(description = "ID пользователя")
    private Long userId;

    @Schema(description = "Email пользователя")
    private String email;

    @Schema(description = "Имя пользователя")
    private String firstName;

    @Schema(description = "Фамилия пользователя")
    private String lastName;

    @Schema(description = "Должность пользователя")
    private String jobTitle;

    @Schema(description = "Компания пользователя")
    private String company;

    @Schema(description = "Местоположение пользователя")
    private String location;

    @Schema(description = "Номер телефона пользователя")
    private String phone;

    @Schema(description = "Роль пользователя")
    private String role;
}