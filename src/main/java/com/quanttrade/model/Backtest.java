package com.quanttrade.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "backtests")
public class Backtest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "strategy_id", nullable = false)
    private Strategy strategy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_id", nullable = false)
    private Stock stock;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Double initialCapital;

    @Column(nullable = false)
    private String status; // PENDING, RUNNING, COMPLETED, FAILED

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "backtest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private BacktestResult result;

    public Backtest() {}

    public Backtest(Strategy strategy, Stock stock, LocalDate startDate, LocalDate endDate, Double initialCapital) {
        this.strategy = strategy;
        this.stock = stock;
        this.startDate = startDate;
        this.endDate = endDate;
        this.initialCapital = initialCapital;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Strategy getStrategy() { return strategy; }
    public void setStrategy(Strategy strategy) { this.strategy = strategy; }
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Double getInitialCapital() { return initialCapital; }
    public void setInitialCapital(Double initialCapital) { this.initialCapital = initialCapital; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public BacktestResult getResult() { return result; }
    public void setResult(BacktestResult result) { this.result = result; }
}
