package com.example.iwork.services.impl;

import com.example.iwork.dto.responses.*;
import com.example.iwork.entities.*;
import com.example.iwork.repositories.*;
import com.example.iwork.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final ReviewRepository reviewRepository;
    private final SalaryRepository salaryRepository;
    private final TaxDataRepository taxDataRepository;
    private final StockDataRepository stockDataRepository;

    @Override
    public PageResponseDto<CompanyResponseDto> getCompanies(
            String search,
            String location,
            String industry,
            Double minRating,
            String size,
            Integer page,
            Integer pageSize
    ) {
        // Create specifications for filtering
        Specification<Company> spec = createSpecification(search, location, industry, minRating, size);

        // Create pageable request
        Pageable pageable = PageRequest.of(
                page != null ? page : 0,
                pageSize != null ? pageSize : 20
        );

        // Query with specifications and pagination
        Page<Company> companiesPage = companyRepository.findAll(spec, pageable);

        // Map companies to DTOs
        List<CompanyResponseDto> companyDtos = companiesPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        // Create page response
        return new PageResponseDto<>(
                companyDtos,
                companiesPage.getNumber(),
                companiesPage.getSize(),
                companiesPage.getTotalElements(),
                companiesPage.getTotalPages(),
                companiesPage.isLast()
        );
    }

    private Specification<Company> createSpecification(
            String search,
            String location,
            String industry,
            Double minRating,
            String size
    ) {
        return (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();

            // Search by name or description
            if (StringUtils.hasText(search)) {
                String searchPattern = "%" + search.toLowerCase() + "%";
                var namePredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        searchPattern
                );
                var descriptionPredicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        searchPattern
                );
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            // Filter by location
            if (StringUtils.hasText(location) && !location.equals("all")) {
                predicates.add(criteriaBuilder.equal(root.get("location"), location));
            }

            // Filter by industry
            if (StringUtils.hasText(industry) && !industry.equals("all")) {
                predicates.add(criteriaBuilder.isMember(
                        industry,
                        root.get("industries")
                ));
            }

            // Filter by minimum rating
            if (minRating != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("rating"),
                        minRating
                ));
            }

            // Filter by company size
            if (StringUtils.hasText(size) && !size.equals("any")) {
                predicates.add(criteriaBuilder.equal(root.get("size"), size));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private CompanyResponseDto mapToDto(Company company) {
        CompanyResponseDto dto = new CompanyResponseDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setDescription(company.getDescription());
        dto.setLocation(company.getLocation());
        dto.setRating(company.getRating());
        dto.setSize(company.getSize());
        dto.setIndustries(company.getIndustries());
        return dto;
    }

    @Override
    public CompanyOverviewResponseDto getCompanyOverview(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Получаем один отзыв (последний добавленный с самым высоким рейтингом)
        Review topReview = company.getReviews().stream()
                .filter(review -> review.getApprovalStatus() == ApprovalStatus.APPROVED).min(Comparator
                        .comparing(Review::getRating, Comparator.reverseOrder())
                        .thenComparing(Review::getCreatedAt, Comparator.reverseOrder()))
                .orElse(null);

        // Получаем одну зарплату (для самой высокооплачиваемой должности)
        Salary topSalary = company.getSalaries().stream()
                .filter(salary -> salary.getApprovalStatus() == ApprovalStatus.APPROVED).max(Comparator.comparing(Salary::getSalary))
                .orElse(null);

        ReviewSummaryDto reviewSummaryDto = null;
        if (topReview != null) {
            reviewSummaryDto = ReviewSummaryDto.builder()
                    .id(topReview.getId())
                    .title(topReview.getTitle())
                    .body(topReview.getBody())
                    .rating(topReview.getRating())
                    .author(topReview.getAuthor())
                    .date(topReview.getDate())
                    .build();
        }

        SalarySummaryDto salarySummaryDto = null;
        if (topSalary != null) {
            // Здесь нам нужно рассчитать минимальную и максимальную зарплату
            // для должности, но поскольку у нас только одна запись,
            // мы можем установить минимум и максимум вокруг фактической зарплаты
            double salary = topSalary.getSalary();
            salarySummaryDto = SalarySummaryDto.builder()
                    .id(topSalary.getId())
                    .position(topSalary.getPosition())
                    .median(salary)
                    .min(Math.max(0, salary * 0.8)) // 80% от зарплаты как минимум
                    .max(salary * 1.2) // 120% от зарплаты как максимум
                    .currency(topSalary.getCurrency())
                    .experienceLevel(topSalary.getExperience())
                    .additionalPay(0.0) // Предполагаем, что у нас нет этих данных
                    .build();
        }

        return CompanyOverviewResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .logoUrl(company.getLogoUrl())
                .bannerImg(company.getBannerImg())
                .description(company.getDescription())
                .location(company.getLocation())
                .rating(company.getRating())
                .size(company.getSize())
                .industries(company.getIndustries())
                .founded(company.getOverallInfo() != null ? company.getOverallInfo().getFounded() : null)
                .revenue(company.getOverallInfo() != null ? company.getOverallInfo().getRevenue() : null)
                .mission(company.getOverallInfo() != null ? company.getOverallInfo().getMission() : null)
                .topReview(reviewSummaryDto)
                .totalReviews((int) company.getReviews().stream()
                        .filter(review -> review.getApprovalStatus() == ApprovalStatus.APPROVED)
                        .count())
                .topSalary(salarySummaryDto)
                .totalSalaries((int) company.getSalaries().stream()
                        .filter(salary -> salary.getApprovalStatus() == ApprovalStatus.APPROVED)
                        .count())
                .build();
    }

    @Override
    public CompanyReviewsResponseDto getCompanyReviews(Long companyId, String sort, String ratingFilter, Integer page, Integer pageSize) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Подготовка параметров сортировки
        Sort sortSpec = switch (sort) {
            case "highest" -> Sort.by(Sort.Direction.DESC, "rating");
            case "lowest" -> Sort.by(Sort.Direction.ASC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        // Подготовка критериев фильтрации по рейтингу
        Double filterRating = null;
        if (!"all".equals(ratingFilter)) {
            try {
                filterRating = Double.parseDouble(ratingFilter);
            } catch (NumberFormatException e) {
                // В случае ошибки парсинга используем фильтр по умолчанию (all)
            }
        }

        // Создаем запрос пагинации
        Pageable pageable = PageRequest.of(page, pageSize, sortSpec);

        // Получаем отзывы с учетом фильтрации
        Page<Review> reviewsPage;
        if (filterRating != null) {
            reviewsPage = reviewRepository.findByCompanyIdAndRatingAndApprovalStatus(
                    companyId, filterRating, ApprovalStatus.APPROVED, pageable);
        } else {
            reviewsPage = reviewRepository.findByCompanyIdAndApprovalStatus(
                    companyId, ApprovalStatus.APPROVED, pageable);
        }

        // Получаем все одобренные отзывы для расчета статистики
        List<Review> allApprovedReviews = reviewRepository.findByCompanyIdAndApprovalStatus(
                companyId, ApprovalStatus.APPROVED);

        // Расчет средней оценки
        double averageRating = allApprovedReviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        // Расчет распределения оценок
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            final int rating = i;
            ratingDistribution.put(rating, (int) allApprovedReviews.stream()
                    .filter(r -> Math.round(r.getRating()) == rating)
                    .count());
        }

        // Преобразование сущностей в DTO
        List<ReviewDto> reviewDtos = reviewsPage.getContent().stream()
                .map(review -> ReviewDto.builder()
                        .id(review.getId())
                        .title(review.getTitle())
                        .body(review.getBody())
                        .pros(review.getPros())
                        .cons(review.getCons())
                        .advice(review.getAdvice())
                        .rating(review.getRating())
                        .careerOpportunities(review.getCareerOpportunities())
                        .workLifeBalance(review.getWorkLifeBalance())
                        .compensation(review.getCompensation())
                        .jobSecurity(review.getJobSecurity())
                        .management(review.getManagement())
                        .position(review.getPosition())
                        .employmentStatus(review.getEmploymentStatus())
                        .employmentType(review.getEmploymentType())
                        .recommendToFriend(review.getRecommendToFriend())
                        .author(review.getAuthor())
                        .helpfulCount(review.getHelpfulCount())
                        .notHelpfulCount(review.getNotHelpfulCount())
                        .commentsCount(review.getCommentsCount())
                        .date(review.getDate())
                        .build())
                .collect(Collectors.toList());

        return CompanyReviewsResponseDto.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .averageRating(averageRating)
                .totalReviews(allApprovedReviews.size())
                .ratingDistribution(ratingDistribution)
                .reviews(reviewDtos)
                .currentPage(page)
                .totalPages(reviewsPage.getTotalPages())
                .pageSize(pageSize)
                .build();
    }

    @Override
    public CompanySalariesResponseDto getCompanySalaries(
            Long companyId, String search, String experienceFilter, String sort, Integer page, Integer pageSize) {

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        // Подготовка параметров сортировки
        Sort sortSpec = switch (sort) {
            case "lowest" -> Sort.by(Sort.Direction.ASC, "salary");
            default -> Sort.by(Sort.Direction.DESC, "salary");
        };

        Pageable pageable = PageRequest.of(page, pageSize, sortSpec);

        // Получаем все одобренные зарплаты для статистики
        List<Salary> allApprovedSalaries = salaryRepository.findByCompanyIdAndApprovalStatus(
                companyId, ApprovalStatus.APPROVED);

        // Фильтрация для пагинированного результата
        Page<Salary> salariesPage;

        // Поиск по должности
        if (search != null && !search.trim().isEmpty()) {
            if (!"all".equals(experienceFilter)) {
                // Поиск с фильтром по опыту
                salariesPage = salaryRepository.findByCompanyIdAndPositionContainingIgnoreCaseAndExperienceContainingIgnoreCaseAndApprovalStatus(
                        companyId, search, experienceFilter, ApprovalStatus.APPROVED, pageable);
            } else {
                // Поиск без фильтра по опыту
                salariesPage = salaryRepository.findByCompanyIdAndPositionContainingIgnoreCaseAndApprovalStatus(
                        companyId, search, ApprovalStatus.APPROVED, pageable);
            }
        } else if (!"all".equals(experienceFilter)) {
            // Только фильтр по опыту
            salariesPage = salaryRepository.findByCompanyIdAndExperienceContainingIgnoreCaseAndApprovalStatus(
                    companyId, experienceFilter, ApprovalStatus.APPROVED, pageable);
        } else {
            // Без фильтров
            salariesPage = salaryRepository.findByCompanyIdAndApprovalStatus(
                    companyId, ApprovalStatus.APPROVED, pageable);
        }

        // Рассчитываем статистику
        SalaryStatistics statistics = calculateSalaryStatistics(allApprovedSalaries);

        // Преобразование сущностей в DTO
        List<SalaryDto> salaryDtos = salariesPage.getContent().stream()
                .map(salary -> {
                    // Рассчитаем медиану, мин и макс на основе похожих должностей
                    List<Double> similarPositionSalaries = allApprovedSalaries.stream()
                            .filter(s -> s.getPosition().equals(salary.getPosition()))
                            .map(Salary::getSalary)
                            .collect(Collectors.toList());

                    double min = similarPositionSalaries.isEmpty() ? salary.getSalary() * 0.8 :
                            Collections.min(similarPositionSalaries);
                    double max = similarPositionSalaries.isEmpty() ? salary.getSalary() * 1.2 :
                            Collections.max(similarPositionSalaries);
                    double median = calculateMedian(similarPositionSalaries);
                    if (median == 0) {
                        median = salary.getSalary();
                    }

                    return SalaryDto.builder()
                            .id(salary.getId())
                            .position(salary.getPosition())
                            .department(salary.getDepartment())
                            .experienceLevel(salary.getExperience())
                            .salary(salary.getSalary())
                            .currency(salary.getCurrency())
                            .payPeriod(salary.getPayPeriod())
                            .median(median)
                            .min(min)
                            .max(max)
                            .additionalPay(0.0) // Предполагаем, что у нас нет этих данных
                            .location(salary.getLocationString())
                            .employmentType(salary.getEmploymentType())
                            .build();
                })
                .collect(Collectors.toList());

        return CompanySalariesResponseDto.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .statistics(statistics)
                .salaries(salaryDtos)
                .currentPage(page)
                .totalPages(salariesPage.getTotalPages())
                .pageSize(pageSize)
                .totalSalaries(allApprovedSalaries.size())
                .build();
    }

    /**
     * Рассчитываем статистику по зарплатам
     */
    private SalaryStatistics calculateSalaryStatistics(List<Salary> salaries) {
        if (salaries.isEmpty()) {
            return SalaryStatistics.builder()
                    .averageSalaryByExperience(new HashMap<>())
                    .averageSalary(0.0)
                    .highestSalary(0.0)
                    .totalPositions(0)
                    .topSalaries(new ArrayList<>())
                    .salaryDistribution(new ArrayList<>())
                    .build();
        }

        // Группируем зарплаты по уровню опыта
        Map<String, List<Double>> salariesByExperience = new HashMap<>();

        // Определяем категории опыта
        Map<String, String> experienceCategories = new HashMap<>();
        experienceCategories.put("entry", "Начальный уровень");
        experienceCategories.put("mid", "Средний уровень");
        experienceCategories.put("senior", "Старший уровень");

        // Распределяем зарплаты по категориям
        for (Salary salary : salaries) {
            String experience = salary.getExperience();
            String category;

            if (experience != null) {
                experience = experience.toLowerCase();
                if (experience.contains("entry") || experience.contains("junior") ||
                        experience.contains("менее 1") || experience.contains("less than 1")) {
                    category = "entry";
                } else if (experience.contains("senior") || experience.contains("lead") ||
                        experience.contains("более 5") || experience.contains("more than 5")) {
                    category = "senior";
                } else {
                    category = "mid";
                }
            } else {
                category = "mid"; // По умолчанию
            }

            if (!salariesByExperience.containsKey(category)) {
                salariesByExperience.put(category, new ArrayList<>());
            }
            salariesByExperience.get(category).add(salary.getSalary());
        }

        // Рассчитываем среднюю зарплату по категориям
        Map<String, Double> averageSalaryByExperience = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : salariesByExperience.entrySet()) {
            String category = experienceCategories.getOrDefault(entry.getKey(), entry.getKey());
            double average = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            averageSalaryByExperience.put(category, average);
        }

        // Рассчитываем среднюю зарплату по всем должностям
        double averageSalary = salaries.stream()
                .mapToDouble(Salary::getSalary)
                .average()
                .orElse(0.0);

        // Находим самую высокую зарплату
        double highestSalary = salaries.stream()
                .mapToDouble(Salary::getSalary)
                .max()
                .orElse(0.0);

        // Определяем уникальные должности для подсчета
        Set<String> uniquePositions = salaries.stream()
                .map(Salary::getPosition)
                .collect(Collectors.toSet());

        // Топ-5 высокооплачиваемых должностей
        Map<String, DoubleSummaryStatistics> salaryByPosition = salaries.stream()
                .collect(Collectors.groupingBy(Salary::getPosition,
                        Collectors.summarizingDouble(Salary::getSalary)));

        List<TopSalaryDto> topSalaries = salaryByPosition.entrySet().stream()
                .map(entry -> TopSalaryDto.builder()
                        .position(entry.getKey())
                        .median(entry.getValue().getAverage())
                        .build())
                .sorted(Comparator.comparing(TopSalaryDto::getMedian, Comparator.reverseOrder()))
                .limit(5)
                .collect(Collectors.toList());

        // Распределение зарплат по диапазонам
        Map<Double, Long> salaryDistribution = salaries.stream()
                .collect(Collectors.groupingBy(
                        salary -> Math.floor(salary.getSalary() / 1000) * 1000,
                        Collectors.counting()));

        List<SalaryDistributionDto> distributionDtos = salaryDistribution.entrySet().stream()
                .map(entry -> SalaryDistributionDto.builder()
                        .salaryRange(entry.getKey())
                        .count(entry.getValue().intValue())
                        .build())
                .sorted(Comparator.comparing(SalaryDistributionDto::getSalaryRange))
                .collect(Collectors.toList());

        return SalaryStatistics.builder()
                .averageSalaryByExperience(averageSalaryByExperience)
                .averageSalary(averageSalary)
                .highestSalary(highestSalary)
                .totalPositions(uniquePositions.size())
                .topSalaries(topSalaries)
                .salaryDistribution(distributionDtos)
                .build();
    }

    private double calculateMedian(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        Collections.sort(values);
        int middle = values.size() / 2;

        if (values.size() % 2 == 1) {
            return values.get(middle);
        } else {
            return (values.get(middle - 1) + values.get(middle)) / 2.0;
        }
    }

    @Override
    public CompanyTaxesResponseDto getCompanyTaxes(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        TaxData taxData = taxDataRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tax data not found"));

        List<YearlyTaxDto> yearlyTaxDtos = taxData.getYearlyTaxes().stream()
                .map(tax -> YearlyTaxDto.builder()
                        .year(tax.getYear())
                        .amount(tax.getAmount())
                        .formattedAmount(tax.getFormattedAmount())
                        .dataSource(tax.getDataSource())
                        .build())
                .collect(Collectors.toList());

        return CompanyTaxesResponseDto.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .registrationDate(taxData.getRegistrationDate())
                .companyStatus(taxData.getCompanyStatus())
                .companyType(taxData.getCompanyType())
                .companySize(taxData.getCompanySize())
                .businessActivity(taxData.getBusinessActivity())
                .businessActivityCode(taxData.getBusinessActivityCode())
                .lastUpdateDate(taxData.getLastUpdateDate())
                .dataSource(taxData.getDataSource())
                .vatPayer(taxData.getVatPayer())
                .astanaHubParticipant(taxData.getAstanaHubParticipant())
                .governmentProcurementParticipant(taxData.getGovernmentProcurementParticipant())
                .licenseCount(taxData.getLicenseCount())
                .lastDocumentChangeDate(taxData.getLastDocumentChangeDate())
                .participationsInOtherCompanies(taxData.getParticipationsInOtherCompanies())
                .yearlyTaxes(yearlyTaxDtos)
                // Здесь должна быть информация о годовой выручке,
                // но в текущих сущностях её нет, поэтому установим нулевые значения
                .annualRevenue(0.0)
                .annualRevenueFormatted("$0")
                .build();
    }

    @Override
    public CompanyStocksResponseDto getCompanyStocks(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));

        StockData stockData = stockDataRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock data not found"));

        List<HistoricalStockDataDto> historicalDataDtos = stockData.getHistoricalData().stream()
                .map(data -> HistoricalStockDataDto.builder()
                        .date(data.getDate())
                        .open(data.getOpen())
                        .high(data.getHigh())
                        .low(data.getLow())
                        .close(data.getClose())
                        .volume(data.getVolume())
                        .build())
                .collect(Collectors.toList());

        return CompanyStocksResponseDto.builder()
                .companyId(companyId)
                .companyName(company.getName())
                .symbol(stockData.getSymbol())
                .currentPrice(stockData.getCurrentPrice())
                .previousClose(stockData.getPreviousClose())
                .open(stockData.getOpen())
                .dayHigh(stockData.getDayHigh())
                .dayLow(stockData.getDayLow())
                .volume(stockData.getVolume())
                .marketCap(stockData.getMarketCap())
                .peRatio(stockData.getPeRatio())
                .dividendYield(stockData.getDividendYield())
                .fiftyTwoWeekHigh(stockData.getFiftyTwoWeekHigh())
                .fiftyTwoWeekLow(stockData.getFiftyTwoWeekLow())
                .currency(stockData.getCurrency())
                .priceChange(stockData.getPriceChange())
                .priceChangePercent(stockData.getPriceChangePercent())
                .formattedPrice(stockData.getFormattedPrice())
                .formattedMarketCap(stockData.getFormattedMarketCap())
                .timestamp(stockData.getTimestamp())
                .historicalData(historicalDataDtos)
                .build();
    }
}