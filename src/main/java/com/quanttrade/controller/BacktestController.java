package com.quanttrade.controller;

import com.quanttrade.dto.RunBacktestRequest;
import com.quanttrade.model.Backtest;
import com.quanttrade.model.Trade;
import com.quanttrade.service.BacktestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/backtests")
public class BacktestController {

    private final BacktestService backtestService;

    public BacktestController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }

    @PostMapping
    public ResponseEntity<?> runBacktest(@RequestBody RunBacktestRequest request) {
        try {
            Backtest backtest = backtestService.runBacktest(request);
            return ResponseEntity.ok(backtest);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Backtest>> getAllBacktests() {
        return ResponseEntity.ok(backtestService.getAllBacktests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Backtest> getBacktestById(@PathVariable Long id) {
        return ResponseEntity.ok(backtestService.getBacktestById(id));
    }

    @GetMapping("/{id}/trades")
    public ResponseEntity<List<Trade>> getBacktestTrades(@PathVariable Long id) {
        return ResponseEntity.ok(backtestService.getBacktestTrades(id));
    }
}
