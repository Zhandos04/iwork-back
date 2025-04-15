package com.example.iwork.dto.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с пагинацией")
public class PageResponseDto<T> {
    @Schema(description = "Список элементов на странице")
    private List<T> content;

    @Schema(description = "Номер текущей страницы", example = "0")
    private int pageNumber;

    @Schema(description = "Размер страницы", example = "20")
    private int pageSize;

    @Schema(description = "Общее количество элементов", example = "1250")
    private long totalElements;

    @Schema(description = "Общее количество страниц", example = "63")
    private int totalPages;

    @Schema(description = "Признак последней страницы", example = "false")
    private boolean last;
}
