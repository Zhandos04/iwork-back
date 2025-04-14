package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.services.impl.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileUploadController {
    private final S3Service s3Service;

    @PostMapping(value = "/upload-file")
    @Operation(
            summary = "Загрузка файла",
            description = "Позволяет загрузить любой файл для пользователя (не только изображение)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл успешно загружен",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "Ошибка загрузки файла", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибка при загрузке файла на сервер", content = @Content)
    })
    public ResponseEntity<Response<?>> uploadFile(@RequestParam("file") MultipartFile file) throws FileUploadException {
        if (file.isEmpty()) {
            throw new FileUploadException("Файл не предоставлен");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new FileUploadException("Файл слишком большой. Максимальный размер — 10MB.");
        }

        try {
            String fileUrl = s3Service.uploadFile(file);
            Response<String> response = new Response<>(fileUrl, "Файл успешно загружен", null, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new FileUploadException("Ошибка при загрузке файла: " + e.getMessage());
        }
    }

}
