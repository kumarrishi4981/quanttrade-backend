package com.quanttrade.repository;

import com.quanttrade.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByBacktestIdOrderByDateAsc(Long backtestId);
}
