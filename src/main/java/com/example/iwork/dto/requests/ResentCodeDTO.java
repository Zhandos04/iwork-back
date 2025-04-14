package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO для повторной отправки кода подтверждения")
public class ResentCodeDTO {

    @Schema(description = "Email пользователя", example = "example@example.com")
    private String email;
}
