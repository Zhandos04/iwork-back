package com.example.iwork.services;

import com.example.iwork.dto.responses.SalaryStatisticsDTO;
public interface SalaryStatisticsService {
    SalaryStatisticsDTO getStatisticsByJobAndLocation(Long jobId, Long locationId);
}