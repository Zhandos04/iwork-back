package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "yearly_taxes")
@Getter
@Setter
public class YearlyTax {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tax_data_id", nullable = false)
    private TaxData taxData;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "tax_amount", nullable = false)
    private Double amount;

    @Column(name = "formatted_amount")
    private String formattedAmount;

    @Column(name = "data_source")
    private String dataSource; // e.g., "kgd.gov.kz"

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
