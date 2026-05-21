package com.quanttrade.service;

import com.quanttrade.model.Stock;
import com.quanttrade.model.StockPrice;
import com.quanttrade.repository.StockPriceRepository;
import com.quanttrade.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    public StockService(StockRepository stockRepository, StockPriceRepository stockPriceRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock getStockById(Long id) {
        return stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock not found with id: " + id));
    }

    public List<StockPrice> getStockPrices(Long stockId) {
        return stockPriceRepository.findByStockIdOrderByDateAsc(stockId);
    }

    public List<StockPrice> getStockPricesInRange(Long stockId, LocalDate start, LocalDate end) {
        return stockPriceRepository.findByStockIdAndDateBetweenOrderByDateAsc(stockId, start, end);
    }

    public long getPriceCount(Long stockId) {
        return stockPriceRepository.countByStockId(stockId);
    }
}
