package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "historical_stock_data")
@Getter
@Setter
public class HistoricalStockData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_data_id", nullable = false)
    private StockData stockData;

    @Column(nullable = false)
    private String date;

    @Column
    private Double open;

    @Column
    private Double high;

    @Column
    private Double low;

    @Column
    private Double close;

    @Column
    private Long volume;
}
