package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "companies")
@Getter
@Setter
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_img")
    private String bannerImg;

    @Column
    private String description;

    @Column
    private String location;

    @Column
    private Double rating;

    @Column
    private String size;

    @ElementCollection
    @CollectionTable(name = "company_industries", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "industry")
    private List<String> industries;

    @Embedded
    private CompanyOverallInfo overallInfo;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    private List<Salary> salaries;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private TaxData taxData;

    @OneToOne(mappedBy = "company", cascade = CascadeType.ALL)
    private StockData stockData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
