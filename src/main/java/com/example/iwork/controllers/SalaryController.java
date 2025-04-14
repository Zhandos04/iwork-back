package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.requests.CreateSalaryDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;
import com.example.iwork.exceptions.ValidationException;
import com.example.iwork.services.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final SalaryService salaryService;

    @PostMapping("/add")
    public ResponseEntity<Response<?>> createSalary(@RequestBody @Valid CreateSalaryDTO createSalaryDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            throw new ValidationException(errorMessages);
        }

        SalaryResponseDTO salaryResponseDTO = salaryService.addSalary(createSalaryDTO);
        Response<SalaryResponseDTO> response = new Response<>(salaryResponseDTO, "Информация о зарплате успешно добавлена", null, HttpStatus.CREATED.value());

        return ResponseEntity.ok(response);
    }
}
