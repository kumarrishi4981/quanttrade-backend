package com.quanttrade.dto;

public class RunBacktestRequest {
    private Long strategyId;
    private Long stockId;
    private String startDate; // yyyy-MM-dd
    private String endDate;   // yyyy-MM-dd
    private Double initialCapital;

    public Long getStrategyId() { return strategyId; }
    public void setStrategyId(Long strategyId) { this.strategyId = strategyId; }
    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public Double getInitialCapital() { return initialCapital; }
    public void setInitialCapital(Double initialCapital) { this.initialCapital = initialCapital; }
}
