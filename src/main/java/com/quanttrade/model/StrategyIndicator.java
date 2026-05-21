package com.quanttrade.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "strategy_indicators")
public class StrategyIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "strategy_id", nullable = false)
    @JsonIgnore
    private Strategy strategy;

    @Column(nullable = false)
    private String indicatorType; // SMA, RSI, MACD, BOLLINGER

    private Integer period1;  // e.g., short SMA period or RSI period
    private Integer period2;  // e.g., long SMA period
    private Double threshold1; // e.g., RSI oversold level (30)
    private Double threshold2; // e.g., RSI overbought level (70)

    public StrategyIndicator() {}

    public StrategyIndicator(String indicatorType, Integer period1, Integer period2, Double threshold1, Double threshold2) {
        this.indicatorType = indicatorType;
        this.period1 = period1;
        this.period2 = period2;
        this.threshold1 = threshold1;
        this.threshold2 = threshold2;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }
    public String getIndicatorType() { return indicatorType; }
    public void setIndicatorType(String indicatorType) { this.indicatorType = indicatorType; }
    public Integer getPeriod1() { return period1; }
    public void setPeriod1(Integer period1) { this.period1 = period1; }
    public Integer getPeriod2() { return period2; }
    public void setPeriod2(Integer period2) { this.period2 = period2; }
    public Double getThreshold1() { return threshold1; }
    public void setThreshold1(Double threshold1) { this.threshold1 = threshold1; }
    public Double getThreshold2() { return threshold2; }
    public void setThreshold2(Double threshold2) { this.threshold2 = threshold2; }
}
