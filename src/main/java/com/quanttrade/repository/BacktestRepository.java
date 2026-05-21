package com.quanttrade.repository;

import com.quanttrade.model.Backtest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BacktestRepository extends JpaRepository<Backtest, Long> {
    List<Backtest> findAllByOrderByCreatedAtDesc();
}
