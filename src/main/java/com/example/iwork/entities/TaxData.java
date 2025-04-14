package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column
    private String cik;

    @Column(name = "data_source")
    private String dataSource;

    @Column(name = "retrieved_at")
    private LocalDateTime retrievedAt;

    @OneToMany(mappedBy = "taxData", cascade = CascadeType.ALL)
    private List<YearlyTax> yearlyTaxes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}