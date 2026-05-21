package com.quanttrade.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backtest_id", nullable = false)
    @JsonIgnore
    private Backtest backtest;

    @Column(nullable = false)
    private String type; // BUY or SELL

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    private Double pnl; // profit/loss for this trade (set on SELL)
    private Double portfolioValue; // portfolio value after this trade
    private String signal; // what triggered this trade

    public Trade() {}

    public Trade(Backtest backtest, String type, LocalDate date, Double price, Integer quantity, Double pnl, Double portfolioValue, String signal) {
        this.backtest = backtest;
        this.type = type;
        this.date = date;
        this.price = price;
        this.quantity = quantity;
        this.pnl = pnl;
        this.portfolioValue = portfolioValue;
        this.signal = signal;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Backtest getBacktest() { return backtest; }
    public void setBacktest(Backtest backtest) { this.backtest = backtest; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getPnl() { return pnl; }
    public void setPnl(Double pnl) { this.pnl = pnl; }
    public Double getPortfolioValue() { return portfolioValue; }
    public void setPortfolioValue(Double portfolioValue) { this.portfolioValue = portfolioValue; }
    public String getSignal() { return signal; }
    public void setSignal(String signal) { this.signal = signal; }
}
