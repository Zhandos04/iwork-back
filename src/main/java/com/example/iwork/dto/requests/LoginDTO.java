package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для запроса авторизации")
public class LoginDTO {

    @Schema(description = "Имя пользователя")
    private String username;

    @Schema(description = "Пароль пользователя", example = "SecureP@ss1")
    private String password;
}
