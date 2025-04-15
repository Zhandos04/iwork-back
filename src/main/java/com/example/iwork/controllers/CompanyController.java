package com.example.iwork.controllers;

import com.example.iwork.dto.Response;
import com.example.iwork.dto.responses.*;
import com.example.iwork.services.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
@Tag(name = "Company Controller", description = "API для работы с компаниями")
public class CompanyController {

    private final CompanyService companyService;

    @Operation(
            summary = "Получение списка компаний",
            description = "Возвращает список компаний с возможностью фильтрации и пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка компаний",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanyResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping
    public ResponseEntity<Response<PageResponseDto<CompanyResponseDto>>> getCompanies(
            @Parameter(description = "Поисковый запрос по названию компании")
            @RequestParam(required = false) String search,

            @Parameter(description = "Фильтр по местоположению")
            @RequestParam(required = false) String location,

            @Parameter(description = "Фильтр по отрасли")
            @RequestParam(required = false) String industry,

            @Parameter(description = "Минимальный рейтинг компании")
            @RequestParam(required = false) Double minRating,

            @Parameter(description = "Фильтр по размеру компании (small, medium, large)")
            @RequestParam(required = false) String size,

            @Parameter(description = "Номер страницы (начиная с 0)")
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(description = "Количество элементов на странице")
            @RequestParam(required = false, defaultValue = "20") Integer pageSize
    ) {
        PageResponseDto<CompanyResponseDto> companies = companyService.getCompanies(
                search, location, industry, minRating, size, page, pageSize
        );

        Response<PageResponseDto<CompanyResponseDto>> response = new Response<>();
        response.setData(companies);
        response.setMessage("Companies retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение обзорной информации о компании",
            description = "Возвращает детальную обзорную информацию о компании по её ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение обзора компании",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanyOverviewResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Компания не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{companyId}/overview")
    public ResponseEntity<Response<CompanyOverviewResponseDto>> getCompanyOverview(
            @Parameter(description = "ID компании", required = true)
            @PathVariable Long companyId
    ) {
        CompanyOverviewResponseDto overview = companyService.getCompanyOverview(companyId);

        Response<CompanyOverviewResponseDto> response = new Response<>();
        response.setData(overview);
        response.setMessage("Company overview retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение отзывов о компании",
            description = "Возвращает отзывы о компании с возможностью фильтрации, сортировки и пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение отзывов о компании",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanyReviewsResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Компания не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{companyId}/reviews")
    public ResponseEntity<Response<CompanyReviewsResponseDto>> getCompanyReviews(
            @Parameter(description = "ID компании", required = true)
            @PathVariable Long companyId,

            @Parameter(
                    description = "Тип сортировки (newest, oldest, highest, lowest)",
                    schema = @Schema(defaultValue = "newest")
            )
            @RequestParam(required = false, defaultValue = "newest") String sort,

            @Parameter(
                    description = "Фильтр по рейтингу (all, 1, 2, 3, 4, 5)",
                    schema = @Schema(defaultValue = "all")
            )
            @RequestParam(required = false, defaultValue = "all") String ratingFilter,

            @Parameter(
                    description = "Номер страницы (начиная с 0)",
                    schema = @Schema(defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(
                    description = "Количество элементов на странице",
                    schema = @Schema(defaultValue = "5")
            )
            @RequestParam(required = false, defaultValue = "5") Integer pageSize
    ) {
        CompanyReviewsResponseDto reviews = companyService.getCompanyReviews(
                companyId, sort, ratingFilter, page, pageSize
        );

        Response<CompanyReviewsResponseDto> response = new Response<>();
        response.setData(reviews);
        response.setMessage("Company reviews retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение данных о зарплатах в компании",
            description = "Возвращает информацию о зарплатах в компании с возможностью фильтрации, сортировки и пагинации"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение данных о зарплатах",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanySalariesResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Компания не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{companyId}/salaries")
    public ResponseEntity<Response<CompanySalariesResponseDto>> getCompanySalaries(
            @Parameter(description = "ID компании", required = true)
            @PathVariable Long companyId,

            @Parameter(description = "Поисковый запрос по должности")
            @RequestParam(required = false) String search,

            @Parameter(
                    description = "Фильтр по опыту работы (all, entry, mid, senior, executive)",
                    schema = @Schema(defaultValue = "all")
            )
            @RequestParam(required = false, defaultValue = "all") String experienceFilter,

            @Parameter(
                    description = "Тип сортировки (highest, lowest)",
                    schema = @Schema(defaultValue = "highest")
            )
            @RequestParam(required = false, defaultValue = "highest") String sort,

            @Parameter(
                    description = "Номер страницы (начиная с 0)",
                    schema = @Schema(defaultValue = "0")
            )
            @RequestParam(required = false, defaultValue = "0") Integer page,

            @Parameter(
                    description = "Количество элементов на странице",
                    schema = @Schema(defaultValue = "5")
            )
            @RequestParam(required = false, defaultValue = "5") Integer pageSize
    ) {
        CompanySalariesResponseDto salaries = companyService.getCompanySalaries(
                companyId, search, experienceFilter, sort, page, pageSize
        );

        Response<CompanySalariesResponseDto> response = new Response<>();
        response.setData(salaries);
        response.setMessage("Company salaries retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение налоговой информации компании",
            description = "Возвращает налоговую информацию о компании"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение налоговой информации",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanyTaxesResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Компания не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{companyId}/taxes")
    public ResponseEntity<Response<CompanyTaxesResponseDto>> getCompanyTaxes(
            @Parameter(description = "ID компании", required = true)
            @PathVariable Long companyId
    ) {
        CompanyTaxesResponseDto taxes = companyService.getCompanyTaxes(companyId);

        Response<CompanyTaxesResponseDto> response = new Response<>();
        response.setData(taxes);
        response.setMessage("Company taxes retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получение информации об акциях компании",
            description = "Возвращает информацию об акциях компании"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение информации об акциях",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CompanyStocksResponseDto.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Компания не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/{companyId}/stocks")
    public ResponseEntity<Response<CompanyStocksResponseDto>> getCompanyStocks(
            @Parameter(description = "ID компании", required = true)
            @PathVariable Long companyId
    ) {
        CompanyStocksResponseDto stocks = companyService.getCompanyStocks(companyId);

        Response<CompanyStocksResponseDto> response = new Response<>();
        response.setData(stocks);
        response.setMessage("Company stocks retrieved successfully");
        response.setError(null);
        response.setCode(HttpStatus.OK.value());

        return ResponseEntity.ok(response);
    }
}