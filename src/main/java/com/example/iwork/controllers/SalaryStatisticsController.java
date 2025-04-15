package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.responses.SalaryStatisticsDTO;
import com.example.iwork.services.SalaryStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/salary-statistics")
@RequiredArgsConstructor
@Tag(name = "Статистика зарплат", description = "API для получения статистики по зарплатам")
public class SalaryStatisticsController {

    private final SalaryStatisticsService salaryStatisticsService;

    @GetMapping
    @Operation(summary = "Получить статистику зарплат",
            description = "Возвращает подробную статистику зарплат по должности и локации")
    public Response<SalaryStatisticsDTO> getSalaryStatistics(
            @Parameter(description = "ID должности", required = true)
            @RequestParam Long jobId,

            @Parameter(description = "ID местоположения", required = true)
            @RequestParam Long locationId) {

        SalaryStatisticsDTO statistics = salaryStatisticsService.getStatisticsByJobAndLocation(jobId, locationId);

        return new Response<>(
                statistics,
                "Статистика зарплат успешно получена",
                null,
                HttpStatus.OK.value()
        );
    }
}