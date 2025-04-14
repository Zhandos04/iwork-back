package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false)
    private Double amount;

    @Column(name = "formatted_amount")
    private String formattedAmount;

    @Column
    private String source;
}
