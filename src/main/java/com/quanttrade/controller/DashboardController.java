package com.quanttrade.controller;

import com.quanttrade.dto.DashboardSummary;
import com.quanttrade.service.BacktestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BacktestService backtestService;

    public DashboardController(BacktestService backtestService) {
        this.backtestService = backtestService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        return ResponseEntity.ok(backtestService.getDashboardSummary());
    }
}
