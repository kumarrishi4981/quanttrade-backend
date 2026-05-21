package com.quanttrade.config;

import com.quanttrade.model.*;
import com.quanttrade.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StrategyRepository strategyRepository;

    public DataLoader(StockRepository stockRepository, StockPriceRepository stockPriceRepository, StrategyRepository strategyRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.strategyRepository = strategyRepository;
    }

    @Override
    public void run(String... args) {
        if (stockRepository.count() > 0) return; // Already seeded

        System.out.println("[QuantTrade] Seeding database with stock data...");

        // Create stocks
        String[][] stockData = {
            {"AAPL", "Apple Inc.", "Technology", "NASDAQ"},
            {"GOOGL", "Alphabet Inc.", "Technology", "NASDAQ"},
            {"MSFT", "Microsoft Corporation", "Technology", "NASDAQ"},
            {"AMZN", "Amazon.com Inc.", "Consumer Cyclical", "NASDAQ"},
            {"TSLA", "Tesla Inc.", "Automotive", "NASDAQ"},
            {"NVDA", "NVIDIA Corporation", "Technology", "NASDAQ"},
            {"JPM", "JPMorgan Chase & Co.", "Financial Services", "NYSE"},
            {"JNJ", "Johnson & Johnson", "Healthcare", "NYSE"}
        };

        double[] startPrices = {150.0, 140.0, 330.0, 170.0, 250.0, 450.0, 155.0, 165.0};
        double[] volatilities = {0.018, 0.020, 0.016, 0.022, 0.035, 0.028, 0.015, 0.012};

        Random rng = new Random(42); // Deterministic seed for reproducibility

        for (int i = 0; i < stockData.length; i++) {
            Stock stock = new Stock(stockData[i][0], stockData[i][1], stockData[i][2], stockData[i][3]);
            stock = stockRepository.save(stock);

            List<StockPrice> prices = generatePriceHistory(stock, startPrices[i], volatilities[i], rng);
            stockPriceRepository.saveAll(prices);
            System.out.println("  -> Loaded " + prices.size() + " price records for " + stock.getSymbol());
        }

        // Create default strategies
        Strategy sma = new Strategy("SMA Crossover (20/50)", "Buy when 20-day SMA crosses above 50-day SMA, sell on cross below", "SMA_CROSSOVER");
        StrategyIndicator smaInd = new StrategyIndicator("SMA", 20, 50, null, null);
        smaInd.setStrategy(sma);
        sma.getIndicators().add(smaInd);
        strategyRepository.save(sma);

        Strategy rsi = new Strategy("RSI Reversal (14)", "Buy when RSI drops below 30 (oversold), sell when RSI rises above 70 (overbought)", "RSI");
        StrategyIndicator rsiInd = new StrategyIndicator("RSI", 14, null, 30.0, 70.0);
        rsiInd.setStrategy(rsi);
        rsi.getIndicators().add(rsiInd);
        strategyRepository.save(rsi);

        Strategy macd = new Strategy("MACD Crossover", "Buy on MACD line crossing above signal line, sell on cross below", "MACD");
        StrategyIndicator macdInd = new StrategyIndicator("MACD", 12, 26, 9.0, null);
        macdInd.setStrategy(macd);
        macd.getIndicators().add(macdInd);
        strategyRepository.save(macd);

        Strategy bb = new Strategy("Bollinger Bands (20,2)", "Buy when price touches lower band, sell when price touches upper band", "BOLLINGER_BANDS");
        StrategyIndicator bbInd = new StrategyIndicator("BOLLINGER", 20, null, 2.0, null);
        bbInd.setStrategy(bb);
        bb.getIndicators().add(bbInd);
        strategyRepository.save(bb);

        System.out.println("[QuantTrade] Database seeding complete!");
    }

    private List<StockPrice> generatePriceHistory(Stock stock, double startPrice, double volatility, Random rng) {
        List<StockPrice> prices = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        double price = startPrice;

        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                double dailyReturn = rng.nextGaussian() * volatility;
                double open = price;
                double close = price * (1 + dailyReturn);
                double high = Math.max(open, close) * (1 + Math.abs(rng.nextGaussian() * volatility * 0.5));
                double low = Math.min(open, close) * (1 - Math.abs(rng.nextGaussian() * volatility * 0.5));
                long volume = (long) (5_000_000 + rng.nextGaussian() * 2_000_000);
                if (volume < 500_000) volume = 500_000;

                close = Math.round(close * 100.0) / 100.0;
                open = Math.round(open * 100.0) / 100.0;
                high = Math.round(high * 100.0) / 100.0;
                low = Math.round(low * 100.0) / 100.0;

                prices.add(new StockPrice(stock, date, open, high, low, close, volume));
                price = close;
            }
            date = date.plusDays(1);
        }
        return prices;
    }
}
