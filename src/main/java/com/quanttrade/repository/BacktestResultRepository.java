package com.quanttrade.repository;

import com.quanttrade.model.BacktestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacktestResultRepository extends JpaRepository<BacktestResult, Long> {
}
