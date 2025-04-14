package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stock_data")
@Getter
@Setter
public class StockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "company_id", nullable = false, unique = true)
    private Company company;

    @Column
    private String symbol;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "previous_close")
    private Double previousClose;

    @Column
    private Double open;

    @Column(name = "day_high")
    private Double dayHigh;

    @Column(name = "day_low")
    private Double dayLow;

    @Column
    private Long volume;

    @Column(name = "market_cap")
    private Long marketCap;

    @Column(name = "pe_ratio")
    private Double peRatio;

    @Column(name = "dividend_yield")
    private Double dividendYield;

    @Column(name = "fifty_two_week_high")
    private Double fiftyTwoWeekHigh;

    @Column(name = "fifty_two_week_low")
    private Double fiftyTwoWeekLow;

    @Column
    private String currency;

    @Column(name = "price_change")
    private Double priceChange;

    @Column(name = "price_change_percent")
    private Double priceChangePercent;

    @Column(name = "formatted_price")
    private String formattedPrice;

    @Column(name = "formatted_market_cap")
    private String formattedMarketCap;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "stockData", cascade = CascadeType.ALL)
    private List<HistoricalStockData> historicalData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}