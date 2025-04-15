package com.example.iwork.services.impl;

import com.example.iwork.dto.responses.SalaryStatisticsDTO;
import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Job;
import com.example.iwork.entities.Location;
import com.example.iwork.entities.Salary;
import com.example.iwork.exceptions.JobNotFoundException;
import com.example.iwork.exceptions.LocationNotFoundException;
import com.example.iwork.repositories.JobRepository;
import com.example.iwork.repositories.LocationRepository;
import com.example.iwork.repositories.SalaryRepository;
import com.example.iwork.services.SalaryStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Реализация сервиса статистики зарплат
 */
@Service
@RequiredArgsConstructor
public class SalaryStatisticsServiceImpl implements SalaryStatisticsService {

    private final SalaryRepository salaryRepository;
    private final JobRepository jobRepository;
    private final LocationRepository locationRepository;

    @Override
    public SalaryStatisticsDTO getStatisticsByJobAndLocation(Long jobId, Long locationId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Должность не найдена с id: " + jobId));

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException("Локация не найдена с id: " + locationId));

        List<Salary> salaries = salaryRepository.findByJobIdAndLocationId(jobId, locationId);

        if (salaries.isEmpty()) {
            return buildEmptyStatistics(job, location);
        }

        // Определяем наиболее часто используемую валюту
        Map<String, Long> currencyCounts = salaries.stream()
                .filter(s -> s.getCurrency() != null)
                .collect(Collectors.groupingBy(Salary::getCurrency, Collectors.counting()));

        String mostCommonCurrency = currencyCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("USD");

        // Определяем наиболее часто используемый период оплаты
        Map<String, Long> payPeriodCounts = salaries.stream()
                .filter(s -> s.getPayPeriod() != null)
                .collect(Collectors.groupingBy(Salary::getPayPeriod, Collectors.counting()));

        String mostCommonPayPeriod = payPeriodCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("yearly");

        return calculateStatistics(salaries, job, location, mostCommonCurrency, mostCommonPayPeriod);
    }

    /**
     * Создать пустую статистику, когда нет данных
     */
    private SalaryStatisticsDTO buildEmptyStatistics(Job job, Location location) {
        return SalaryStatisticsDTO.builder()
                .jobTitle(job.getTitle())
                .location(location.getLocationValue())
                .sampleSize(0L)
                .build();
    }

    /**
     * Рассчитать полную статистику на основе данных о зарплатах
     */
    private SalaryStatisticsDTO calculateStatistics(
            List<Salary> salaries, Job job, Location location, String targetCurrency, String targetPayPeriod) {

        // Фильтруем подтвержденные зарплаты в указанной валюте
        List<Salary> filteredSalaries = salaries.stream()
                .filter(s -> s.getSalary() != null
                        && s.getApprovalStatus() == ApprovalStatus.APPROVED
                        && (s.getCurrency() == null || s.getCurrency().equals(targetCurrency)))
                .toList();

        if (filteredSalaries.isEmpty()) {
            return buildEmptyStatistics(job, location);
        }

        // Нормализуем зарплаты к годовой/месячной в зависимости от targetPayPeriod
        List<Double> normalizedSalaries = filteredSalaries.stream()
                .map(s -> normalizeSalary(s, targetPayPeriod))
                .toList();

        // Сортируем зарплаты для расчета перцентилей
        List<Double> sortedSalaries = new ArrayList<>(normalizedSalaries);
        sortedSalaries.sort(Double::compareTo);

        int size = sortedSalaries.size();

        // Рассчитываем базовую статистику
        Double min = sortedSalaries.get(0);
        Double max = sortedSalaries.get(size - 1);
        Double average = sortedSalaries.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // Рассчитываем медиану
        Double median;
        if (size % 2 == 0) {
            median = (sortedSalaries.get(size / 2 - 1) + sortedSalaries.get(size / 2)) / 2.0;
        } else {
            median = sortedSalaries.get(size / 2);
        }

        // Расчет перцентилей
        Double p10 = calculatePercentile(sortedSalaries, 10);
        Double p25 = calculatePercentile(sortedSalaries, 25);
        Double p75 = calculatePercentile(sortedSalaries, 75);
        Double p90 = calculatePercentile(sortedSalaries, 90);

        // Распределение по типам занятости
        Map<String, Long> employmentTypeDistribution = filteredSalaries.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getEmploymentType() != null ? s.getEmploymentType() : "Не указано",
                        Collectors.counting()
                ));

        // Распределение по уровню опыта
        Map<String, Long> experienceLevelDistribution = filteredSalaries.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getExperience() != null ? s.getExperience() : "Не указано",
                        Collectors.counting()
                ));

        // Средняя зарплата по уровню опыта
        Map<String, Double> salaryByExperienceLevel = filteredSalaries.stream()
                .filter(s -> s.getExperience() != null)
                .collect(Collectors.groupingBy(
                        Salary::getExperience,
                        Collectors.averagingDouble(s -> normalizeSalary(s, targetPayPeriod))
                ));

        return SalaryStatisticsDTO.builder()
                .jobTitle(job.getTitle())
                .location(location.getLocationValue())
                .averageSalary(average)
                .minSalary(min)
                .maxSalary(max)
                .medianSalary(median)
                .percentile10(p10)
                .percentile25(p25)
                .percentile75(p75)
                .percentile90(p90)
                .sampleSize((long) filteredSalaries.size())
                .currency(targetCurrency)
                .payPeriod(targetPayPeriod)
                .employmentTypeDistribution(employmentTypeDistribution)
                .experienceLevelDistribution(experienceLevelDistribution)
                .salaryByExperienceLevel(salaryByExperienceLevel)
                .build();
    }

    /**
     * Рассчитать перцентиль для отсортированного списка значений
     */
    private Double calculatePercentile(List<Double> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) {
            return 0.0;
        }

        int index = (int) Math.ceil(percentile / 100.0 * sortedValues.size()) - 1;
        index = Math.max(0, Math.min(sortedValues.size() - 1, index));
        return sortedValues.get(index);
    }

    /**
     * Нормализовать зарплату к указанному периоду оплаты
     */
    private Double normalizeSalary(Salary salary, String targetPayPeriod) {
        Double value = salary.getSalary();

        // Проверяем, нужна ли конвертация между месячной и годовой зарплатой
        if (salary.getPayPeriod() != null && targetPayPeriod != null) {
            if (salary.getPayPeriod().equals("monthly") && targetPayPeriod.equals("yearly")) {
                return value * 12;
            } else if (salary.getPayPeriod().equals("yearly") && targetPayPeriod.equals("monthly")) {
                return value / 12;
            }
        }

        return value;
    }
}