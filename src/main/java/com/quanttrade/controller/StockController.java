package com.quanttrade.controller;

import com.quanttrade.model.Stock;
import com.quanttrade.model.StockPrice;
import com.quanttrade.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStockById(@PathVariable Long id) {
        Stock stock = stockService.getStockById(id);
        long priceCount = stockService.getPriceCount(id);
        Map<String, Object> response = new HashMap<>();
        response.put("stock", stock);
        response.put("priceCount", priceCount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/prices")
    public ResponseEntity<List<StockPrice>> getStockPrices(@PathVariable Long id) {
        return ResponseEntity.ok(stockService.getStockPrices(id));
    }
}
