package com.quanttrade.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "backtest_results")
public class BacktestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "backtest_id", nullable = false)
    @JsonIgnore
    private Backtest backtest;

    private Double finalCapital;
    private Double totalReturn;      // percentage
    private Double cagr;             // compound annual growth rate
    private Double sharpeRatio;
    private Double maxDrawdown;      // percentage
    private Double winRate;          // percentage
    private Double profitFactor;
    private Integer totalTrades;
    private Integer profitableTrades;
    private Integer losingTrades;
    private Double avgWin;
    private Double avgLoss;
    private Double largestWin;
    private Double largestLoss;

    public BacktestResult() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Backtest getBacktest() { return backtest; }
    public void setBacktest(Backtest backtest) { this.backtest = backtest; }
    public Double getFinalCapital() { return finalCapital; }
    public void setFinalCapital(Double finalCapital) { this.finalCapital = finalCapital; }
    public Double getTotalReturn() { return totalReturn; }
    public void setTotalReturn(Double totalReturn) { this.totalReturn = totalReturn; }
    public Double getCagr() { return cagr; }
    public void setCagr(Double cagr) { this.cagr = cagr; }
    public Double getSharpeRatio() { return sharpeRatio; }
    public void setSharpeRatio(Double sharpeRatio) { this.sharpeRatio = sharpeRatio; }
    public Double getMaxDrawdown() { return maxDrawdown; }
    public void setMaxDrawdown(Double maxDrawdown) { this.maxDrawdown = maxDrawdown; }
    public Double getWinRate() { return winRate; }
    public void setWinRate(Double winRate) { this.winRate = winRate; }
    public Double getProfitFactor() { return profitFactor; }
    public void setProfitFactor(Double profitFactor) { this.profitFactor = profitFactor; }
    public Integer getTotalTrades() { return totalTrades; }
    public void setTotalTrades(Integer totalTrades) { this.totalTrades = totalTrades; }
    public Integer getProfitableTrades() { return profitableTrades; }
    public void setProfitableTrades(Integer profitableTrades) { this.profitableTrades = profitableTrades; }
    public Integer getLosingTrades() { return losingTrades; }
    public void setLosingTrades(Integer losingTrades) { this.losingTrades = losingTrades; }
    public Double getAvgWin() { return avgWin; }
    public void setAvgWin(Double avgWin) { this.avgWin = avgWin; }
    public Double getAvgLoss() { return avgLoss; }
    public void setAvgLoss(Double avgLoss) { this.avgLoss = avgLoss; }
    public Double getLargestWin() { return largestWin; }
    public void setLargestWin(Double largestWin) { this.largestWin = largestWin; }
    public Double getLargestLoss() { return largestLoss; }
    public void setLargestLoss(Double largestLoss) { this.largestLoss = largestLoss; }
}
