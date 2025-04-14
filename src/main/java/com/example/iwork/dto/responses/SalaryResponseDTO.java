package com.example.iwork.dto.responses;

import lombok.Data;

@Data
public class SalaryResponseDTO {
    private Long id;

    private Long companyId;

    private String companyName;

    private String position;

    private String department;

    private String employmentStatus; // current, former

    private String employmentType; // full-time, part-time, contract, internship, freelance

    private Double amount;

    private String currency;

    private String payPeriod; // monthly, yearly

    private String formattedAmount; // Отформатированная сумма с валютой

    private String bonuses;

    private String stockOptions;

    private String experience;

    private String location;

    private Boolean anonymous;

    private String approvalStatus; // PENDING, APPROVED, REJECTED

    private Boolean hasVerification; // Наличие подтверждающего документа

    private String date;
}