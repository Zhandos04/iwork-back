package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления пароля пользователя")
public class PasswordDTO {

    @Schema(description = "Старый пароль пользователя", example = "OldPass123!")
    private String oldPassword;

    @Schema(description = "Новый пароль (минимум 8 символов, содержит заглавную букву, цифру и специальный символ)", example = "SecureP@ss1")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#.])(?=.*[a-z])[A-Za-z\\d@$!%*?&_#.]{8,}$",
            message = "Password должен содержать как минимум одну заглавную букву, одну цифру, один специальный символ и быть длиной не менее 8 символов.")
    private String newPassword;
}
