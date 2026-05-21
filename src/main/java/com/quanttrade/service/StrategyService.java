package com.quanttrade.service;

import com.quanttrade.dto.CreateStrategyRequest;
import com.quanttrade.model.Strategy;
import com.quanttrade.model.StrategyIndicator;
import com.quanttrade.repository.StrategyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StrategyService {

    private final StrategyRepository strategyRepository;

    public StrategyService(StrategyRepository strategyRepository) {
        this.strategyRepository = strategyRepository;
    }

    public List<Strategy> getAllStrategies() {
        return strategyRepository.findAll();
    }

    public Strategy getStrategyById(Long id) {
        return strategyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Strategy not found with id: " + id));
    }

    public Strategy createStrategy(CreateStrategyRequest request) {
        Strategy strategy = new Strategy(request.getName(), request.getDescription(), request.getType());

        if (request.getIndicators() != null) {
            for (CreateStrategyRequest.IndicatorConfig config : request.getIndicators()) {
                StrategyIndicator indicator = new StrategyIndicator(
                        config.getIndicatorType(),
                        config.getPeriod1(),
                        config.getPeriod2(),
                        config.getThreshold1(),
                        config.getThreshold2()
                );
                indicator.setStrategy(strategy);
                strategy.getIndicators().add(indicator);
            }
        }

        return strategyRepository.save(strategy);
    }

    public void deleteStrategy(Long id) {
        strategyRepository.deleteById(id);
    }
}
