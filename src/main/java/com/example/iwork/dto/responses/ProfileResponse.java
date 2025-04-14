package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO с информацией о профиле пользователя")
public class ProfileResponse {

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

