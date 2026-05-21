package com.quanttrade.controller;

import com.quanttrade.dto.CreateStrategyRequest;
import com.quanttrade.model.Strategy;
import com.quanttrade.service.StrategyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/strategies")
public class StrategyController {

    private final StrategyService strategyService;

    public StrategyController(StrategyService strategyService) {
        this.strategyService = strategyService;
    }

    @GetMapping
    public ResponseEntity<List<Strategy>> getAllStrategies() {
        return ResponseEntity.ok(strategyService.getAllStrategies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Strategy> getStrategyById(@PathVariable Long id) {
        return ResponseEntity.ok(strategyService.getStrategyById(id));
    }

    @PostMapping
    public ResponseEntity<Strategy> createStrategy(@RequestBody CreateStrategyRequest request) {
        return ResponseEntity.ok(strategyService.createStrategy(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStrategy(@PathVariable Long id) {
        strategyService.deleteStrategy(id);
        return ResponseEntity.noContent().build();
    }
}
