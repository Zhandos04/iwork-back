package com.example.iwork.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalaryStatisticsDTO {
    private String jobTitle;
    private String location;
    private Double averageSalary;
    private Double minSalary;
    private Double maxSalary;
    private Double medianSalary;
    private Double percentile10;
    private Double percentile25;
    private Double percentile75;
    private Double percentile90;
    private Long sampleSize;
    private String currency;
    private String payPeriod;
    private Map<String, Long> employmentTypeDistribution;
    private Map<String, Long> experienceLevelDistribution;
    private Map<String, Double> salaryByExperienceLevel;
}