package com.quanttrade.service;

import com.quanttrade.dto.DashboardSummary;
import com.quanttrade.dto.RunBacktestRequest;
import com.quanttrade.model.*;
import com.quanttrade.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BacktestService {

    private final BacktestRepository backtestRepository;
    private final BacktestResultRepository backtestResultRepository;
    private final TradeRepository tradeRepository;
    private final StockService stockService;
    private final StrategyService strategyService;

    public BacktestService(BacktestRepository backtestRepository, BacktestResultRepository backtestResultRepository,
                           TradeRepository tradeRepository, StockService stockService, StrategyService strategyService) {
        this.backtestRepository = backtestRepository;
        this.backtestResultRepository = backtestResultRepository;
        this.tradeRepository = tradeRepository;
        this.stockService = stockService;
        this.strategyService = strategyService;
    }

    @Transactional
    public Backtest runBacktest(RunBacktestRequest request) {
        Strategy strategy = strategyService.getStrategyById(request.getStrategyId());
        Stock stock = stockService.getStockById(request.getStockId());

        LocalDate startDate = LocalDate.parse(request.getStartDate());
        LocalDate endDate = LocalDate.parse(request.getEndDate());
        double initialCapital = request.getInitialCapital() != null ? request.getInitialCapital() : 100000.0;

        Backtest backtest = new Backtest(strategy, stock, startDate, endDate, initialCapital);
        backtest.setStatus("RUNNING");
        backtest = backtestRepository.save(backtest);

        try {
            List<StockPrice> prices = stockService.getStockPricesInRange(stock.getId(), startDate, endDate);

            if (prices.size() < 50) {
                backtest.setStatus("FAILED");
                backtestRepository.save(backtest);
                throw new RuntimeException("Insufficient price data. Need at least 50 trading days, got " + prices.size());
            }

            BacktestEngine engine = new BacktestEngine(prices, strategy, initialCapital, backtest);
            BacktestEngine.EngineResult engineResult = engine.execute();

            backtestResultRepository.save(engineResult.result);
            tradeRepository.saveAll(engineResult.trades);

            backtest.setStatus("COMPLETED");
            backtest.setResult(engineResult.result);
            backtestRepository.save(backtest);

        } catch (Exception e) {
            if (!"FAILED".equals(backtest.getStatus())) {
                backtest.setStatus("FAILED");
                backtestRepository.save(backtest);
            }
            throw e;
        }

        return backtest;
    }

    public List<Backtest> getAllBacktests() {
        return backtestRepository.findAllByOrderByCreatedAtDesc();
    }

    public Backtest getBacktestById(Long id) {
        return backtestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Backtest not found with id: " + id));
    }

    public List<Trade> getBacktestTrades(Long backtestId) {
        return tradeRepository.findByBacktestIdOrderByDateAsc(backtestId);
    }

    public DashboardSummary getDashboardSummary() {
        DashboardSummary summary = new DashboardSummary();
        summary.setTotalStocks((int) stockService.getAllStocks().size());
        summary.setTotalStrategies((int) strategyService.getAllStrategies().size());

        List<Backtest> allBacktests = backtestRepository.findAllByOrderByCreatedAtDesc();
        summary.setTotalBacktests(allBacktests.size());

        List<Backtest> completed = allBacktests.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus()) && b.getResult() != null)
                .collect(Collectors.toList());
        summary.setCompletedBacktests(completed.size());

        if (!completed.isEmpty()) {
            Backtest best = completed.stream()
                    .max(Comparator.comparingDouble(b -> b.getResult().getTotalReturn()))
                    .orElse(null);
            if (best != null) {
                summary.setBestReturn(best.getResult().getTotalReturn());
                summary.setBestReturnStock(best.getStock().getSymbol());
                summary.setBestReturnStrategy(best.getStrategy().getName());
            }
            summary.setAvgReturn(Math.round(completed.stream()
                    .mapToDouble(b -> b.getResult().getTotalReturn())
                    .average().orElse(0) * 100.0) / 100.0);
            summary.setAvgSharpeRatio(Math.round(completed.stream()
                    .mapToDouble(b -> b.getResult().getSharpeRatio())
                    .average().orElse(0) * 100.0) / 100.0);
        }

        // Recent backtests (last 5)
        List<Map<String, Object>> recent = allBacktests.stream().limit(5).map(b -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", b.getId());
            map.put("stock", b.getStock().getSymbol());
            map.put("strategy", b.getStrategy().getName());
            map.put("status", b.getStatus());
            map.put("totalReturn", b.getResult() != null ? b.getResult().getTotalReturn() : null);
            map.put("createdAt", b.getCreatedAt());
            return map;
        }).collect(Collectors.toList());
        summary.setRecentBacktests(recent);

        return summary;
    }
}
