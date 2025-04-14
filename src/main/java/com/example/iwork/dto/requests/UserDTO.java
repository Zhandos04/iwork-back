package com.example.iwork.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO для регистрации и обновления пользователя")
public class UserDTO {
    @Schema(description = "Имя пользователя (от 3 до 20 символов, только буквы, цифры, подчеркивания и точки)", example = "john_doe99")
    @Pattern(
            regexp = "^(?=.{3,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z][a-zA-Z0-9._]+(?<![_.])$",
            message = "Username должен начинаться с буквы, содержать от 3 до 20 символов, включать только буквы, цифры, точки и подчёркивания, не начинаться и не заканчиваться на . или _, и не содержать подряд идущих . или _."
    )
    private String username;
    @Schema(description = "Email пользователя", example = "example@example.com")
    @Email(message = "Неверный формат email")
    private String email;
    @Schema(description = "Пароль (минимум 8 символов, содержит заглавную букву, цифру и специальный символ)", example = "SecureP@ss1")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_#.])(?=.*[a-z])[A-Za-z\\d@$!%*?&_#.]{8,}$",
            message = "Password должен содержать как минимум одну заглавную букву, одну цифру, один специальный символ и быть длиной не менее 8 символов.")
    private String password;
}
