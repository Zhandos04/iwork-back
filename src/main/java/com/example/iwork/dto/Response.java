package com.example.iwork.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Стандартный ответ API")
public class Response<T> {

    @Schema(description = "Данные ответа", example = "[...]")
    private T data;

    @Schema(description = "Сообщение о результате", example = "Announcements found")
    private String message;

    @Schema(description = "Ошибка (если есть)", example = "null")
    private String error;

    @Schema(description = "HTTP-код ответа", example = "200")
    private int code;
}