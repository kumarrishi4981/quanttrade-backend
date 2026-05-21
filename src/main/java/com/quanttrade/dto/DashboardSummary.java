package com.quanttrade.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummary {
    private int totalStocks;
    private int totalStrategies;
    private int totalBacktests;
    private int completedBacktests;
    private Double bestReturn;
    private String bestReturnStock;
    private String bestReturnStrategy;
    private Double avgReturn;
    private Double avgSharpeRatio;
    private List<Map<String, Object>> recentBacktests;

    // Getters and Setters
    public int getTotalStocks() { return totalStocks; }
    public void setTotalStocks(int totalStocks) { this.totalStocks = totalStocks; }
    public int getTotalStrategies() { return totalStrategies; }
    public void setTotalStrategies(int totalStrategies) { this.totalStrategies = totalStrategies; }
    public int getTotalBacktests() { return totalBacktests; }
    public void setTotalBacktests(int totalBacktests) { this.totalBacktests = totalBacktests; }
    public int getCompletedBacktests() { return completedBacktests; }
    public void setCompletedBacktests(int completedBacktests) { this.completedBacktests = completedBacktests; }
    public Double getBestReturn() { return bestReturn; }
    public void setBestReturn(Double bestReturn) { this.bestReturn = bestReturn; }
    public String getBestReturnStock() { return bestReturnStock; }
    public void setBestReturnStock(String bestReturnStock) { this.bestReturnStock = bestReturnStock; }
    public String getBestReturnStrategy() { return bestReturnStrategy; }
    public void setBestReturnStrategy(String bestReturnStrategy) { this.bestReturnStrategy = bestReturnStrategy; }
    public Double getAvgReturn() { return avgReturn; }
    public void setAvgReturn(Double avgReturn) { this.avgReturn = avgReturn; }
    public Double getAvgSharpeRatio() { return avgSharpeRatio; }
    public void setAvgSharpeRatio(Double avgSharpeRatio) { this.avgSharpeRatio = avgSharpeRatio; }
    public List<Map<String, Object>> getRecentBacktests() { return recentBacktests; }
    public void setRecentBacktests(List<Map<String, Object>> recentBacktests) { this.recentBacktests = recentBacktests; }
}
