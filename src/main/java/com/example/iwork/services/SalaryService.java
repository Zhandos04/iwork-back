package com.example.iwork.services;

import com.example.iwork.dto.requests.CreateSalaryDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;

public interface SalaryService {
    SalaryResponseDTO addSalary(CreateSalaryDTO createSalaryDTO);
}
