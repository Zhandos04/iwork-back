package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "salaries")
@Getter
@Setter
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String position;

    @Column
    private String department;

    @Column(name = "employment_status")
    private String employmentStatus; // current, former

    @Column(name = "employment_type")
    private String employmentType; // full-time, part-time, contract, etc.

    @Column(nullable = false)
    private Double amount;

    @Column
    private Double min;

    @Column
    private Double max;

    @Column
    private Double median;

    @Column
    private String currency;

    @Column(name = "pay_period")
    private String payPeriod; // monthly, yearly

    @Column(name = "additional_pay")
    private Double additionalPay;

    @Column
    private String bonuses;

    @Column(name = "stock_options")
    private String stockOptions;

    @Column(name = "experience_level")
    private String experienceLevel;

    @Column
    private String location;

    @Column
    private Boolean anonymous;

    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @Column(name = "contract_document_url")
    private String contractDocumentUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

