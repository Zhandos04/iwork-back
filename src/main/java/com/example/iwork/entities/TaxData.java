package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tax_data")
@Getter
@Setter
public class TaxData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "company_status")
    private String companyStatus;

    @Column(name = "company_type")
    private String companyType; // АО, ТОО, etc.

    @Column(name = "company_size")
    private String companySize;

    @Column(name = "business_activity")
    private String businessActivity;

    @Column(name = "business_activity_code")
    private String businessActivityCode; // ОКЭД code

    @Column(name = "last_update_date")
    private LocalDate lastUpdateDate;

    @Column(name = "data_source")
    private String dataSource; // e.g., "kgd.gov.kz", "stat.gov.kz"

    @Column(name = "vat_payer")
    private Boolean vatPayer;

    @Column(name = "astana_hub_participant")
    private Boolean astanaHubParticipant;

    @Column(name = "government_procurement_participant")
    private Boolean governmentProcurementParticipant;

    @Column(name = "license_count")
    private Integer licenseCount;

    @Column(name = "last_document_change_date")
    private LocalDate lastDocumentChangeDate;

    @Column(name = "participations_in_other_companies")
    private Integer participationsInOtherCompanies;

    @OneToMany(mappedBy = "taxData", cascade = CascadeType.ALL)
    private List<YearlyTax> yearlyTaxes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}