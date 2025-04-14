package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для запроса авторизации")
public class LoginDTO {

    @Schema(description = "Email пользователя", example = "example@example.com")
    private String email;

    @Schema(description = "Пароль пользователя", example = "SecureP@ss1")
    private String password;
}
