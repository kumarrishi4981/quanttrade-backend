package com.quanttrade.repository;

import com.quanttrade.model.StrategyIndicator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategyIndicatorRepository extends JpaRepository<StrategyIndicator, Long> {
}
