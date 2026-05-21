package com.quanttrade.repository;

import com.quanttrade.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockIdOrderByDateAsc(Long stockId);
    List<StockPrice> findByStockIdAndDateBetweenOrderByDateAsc(Long stockId, LocalDate start, LocalDate end);
    long countByStockId(Long stockId);
}
