package com.example.iwork.services;


import com.example.iwork.dto.responses.*;

public interface CompanyService {
    PageResponseDto<CompanyResponseDto> getCompanies(
            String search,
            String location,
            String industry,
            Double minRating,
            String size,
            Integer page,
            Integer pageSize
    );
    CompanyOverviewResponseDto getCompanyOverview(Long companyId);

    /**
     * Получение отзывов о компании с фильтрацией и сортировкой
     */
    CompanyReviewsResponseDto getCompanyReviews(Long companyId, String sort, String ratingFilter, Integer page, Integer pageSize);

    /**
     * Получение данных о зарплатах в компании с фильтрацией и сортировкой
     */
    CompanySalariesResponseDto getCompanySalaries(Long companyId, String search, String experienceFilter, String sort, Integer page, Integer pageSize);

    /**
     * Получение налоговой информации компании
     */
    CompanyTaxesResponseDto getCompanyTaxes(Long companyId);

    /**
     * Получение информации об акциях компании
     */
    CompanyStocksResponseDto getCompanyStocks(Long companyId);
}