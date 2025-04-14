package com.example.iwork.services.impl;

import com.example.iwork.dto.requests.CreateSalaryDTO;
import com.example.iwork.dto.responses.SalaryResponseDTO;
import com.example.iwork.entities.ApprovalStatus;
import com.example.iwork.entities.Company;
import com.example.iwork.entities.Salary;
import com.example.iwork.entities.User;
import com.example.iwork.exceptions.CompanyNotFoundException;
import com.example.iwork.repositories.CompanyRepository;
import com.example.iwork.repositories.SalaryRepository;
import com.example.iwork.services.SalaryService;
import com.example.iwork.services.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final CompanyRepository companyRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SalaryResponseDTO addSalary(CreateSalaryDTO createSalaryDTO) {
        Company company = companyRepository.findById(createSalaryDTO.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found!"));

        User user = userService.getUserByUsername(userService.getCurrentUser().getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        Salary salary = modelMapper.map(createSalaryDTO, Salary.class);
        salary.setCompany(company);
        salary.setUser(user);
        salary.setApprovalStatus(ApprovalStatus.PENDING);
        salary.setCreatedAt(LocalDateTime.now());
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("LLLL yyyy", new Locale("ru"));
        String formattedDate = formatter.format(salary.getCreatedAt());
        salary.setDate(formattedDate.substring(0, 1).toUpperCase() +
                formattedDate.substring(1));

        Salary savedSalary = salaryRepository.save(salary);

        SalaryResponseDTO responseDTO = modelMapper.map(savedSalary, SalaryResponseDTO.class);
        responseDTO.setCompanyName(company.getName());

        String currencySymbol = getCurrencySymbol(savedSalary.getCurrency());
        responseDTO.setFormattedAmount(currencySymbol + savedSalary.getSalary() +
                " " + (savedSalary.getPayPeriod().equals("monthly") ? "в месяц" : "в год"));

        responseDTO.setHasVerification(savedSalary.getContractDocumentUrl() != null &&
                !savedSalary.getContractDocumentUrl().isEmpty());

        return responseDTO;
    }

    private String getCurrencySymbol(String currency) {
        return switch (currency) {
            case "USD" -> "$";
            case "EUR" -> "€";
            case "KZT" -> "₸";
            case "RUB" -> "₽";
            default -> "";
        };
    }
}
