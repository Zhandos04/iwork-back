package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "DTO для верификации кода")
public class CodeDTO {

    @Schema(description = "Email пользователя", example = "example@example.com")
    private String email;

    @Schema(description = "Код подтверждения", example = "123456")
    private String code;
}
