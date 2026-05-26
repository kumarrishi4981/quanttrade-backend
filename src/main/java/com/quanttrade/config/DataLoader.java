package com.quanttrade.config;

import com.quanttrade.model.*;
import com.quanttrade.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private final StockRepository stockRepository;
    private final StrategyRepository strategyRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataLoader(StockRepository stockRepository, StrategyRepository strategyRepository, JdbcTemplate jdbcTemplate) {
        this.stockRepository = stockRepository;
        this.strategyRepository = strategyRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) {
        long currentCount = stockRepository.count();
        if (currentCount >= 28) {
            System.out.println("[QuantTrade] Database already fully seeded (" + currentCount + " stocks). Skipping.");
            return;
        }

        System.out.println("[QuantTrade] Seeding database with stock data...");

        // If partially seeded, wipe the database first using high-performance TRUNCATE CASCADE
        if (currentCount > 0) {
            System.out.println("[QuantTrade] Partial seeding detected (" + currentCount + " stocks). Wiping database using SQL TRUNCATE to reset sequences...");
            try {
                jdbcTemplate.execute("TRUNCATE TABLE backtest_results CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE backtests CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE trades CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE stock_prices CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE strategy_indicators CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE strategies CASCADE");
                jdbcTemplate.execute("TRUNCATE TABLE stocks CASCADE");
                System.out.println("[QuantTrade] Database successfully cleared!");
            } catch (Exception e) {
                System.err.println("[QuantTrade] SQL Truncate failed, falling back to repository delete: " + e.getMessage());
                strategyRepository.deleteAll();
                stockRepository.deleteAll();
            }
        }

        // ── International stocks ───────────────────────────────────────────
        String[][] intlStockData = {
            {"AAPL", "Apple Inc.", "Technology", "NASDAQ"},
            {"GOOGL", "Alphabet Inc.", "Technology", "NASDAQ"},
            {"MSFT", "Microsoft Corporation", "Technology", "NASDAQ"},
            {"AMZN", "Amazon.com Inc.", "Consumer Cyclical", "NASDAQ"},
            {"TSLA", "Tesla Inc.", "Automotive", "NASDAQ"},
            {"NVDA", "NVIDIA Corporation", "Technology", "NASDAQ"},
            {"JPM", "JPMorgan Chase & Co.", "Financial Services", "NYSE"},
            {"JNJ", "Johnson & Johnson", "Healthcare", "NYSE"}
        };

        double[] intlStartPrices = {150.0, 140.0, 330.0, 170.0, 250.0, 450.0, 155.0, 165.0};
        double[] intlVolatilities = {0.018, 0.020, 0.016, 0.022, 0.035, 0.028, 0.015, 0.012};

        // ── Indian stocks (NSE, prices in INR) ─────────────────────────────
        String[][] indianStockData = {
            {"RELIANCE", "Reliance Industries Ltd", "Energy", "NSE"},
            {"TCS", "Tata Consultancy Services", "Technology", "NSE"},
            {"INFY", "Infosys Ltd", "Technology", "NSE"},
            {"HDFCBANK", "HDFC Bank Ltd", "Financial Services", "NSE"},
            {"ICICIBANK", "ICICI Bank Ltd", "Financial Services", "NSE"},
            {"ITC", "ITC Ltd", "Consumer Goods", "NSE"},
            {"BHARTIARTL", "Bharti Airtel Ltd", "Telecom", "NSE"},
            {"SBIN", "State Bank of India", "Financial Services", "NSE"},
            {"WIPRO", "Wipro Ltd", "Technology", "NSE"},
            {"TATAMOTORS", "Tata Motors Ltd", "Automotive", "NSE"},
            {"HCLTECH", "HCL Technologies Ltd", "Technology", "NSE"},
            {"SUNPHARMA", "Sun Pharmaceutical", "Healthcare", "NSE"},
            {"KOTAKBANK", "Kotak Mahindra Bank", "Financial Services", "NSE"},
            {"MARUTI", "Maruti Suzuki India", "Automotive", "NSE"},
            {"ADANIENT", "Adani Enterprises Ltd", "Infrastructure", "NSE"},
            {"BAJFINANCE", "Bajaj Finance Ltd", "Financial Services", "NSE"},
            {"LTIM", "LTIMindtree Ltd", "Technology", "NSE"},
            {"TITAN", "Titan Company Ltd", "Consumer Goods", "NSE"},
            {"AXISBANK", "Axis Bank Ltd", "Financial Services", "NSE"},
            {"HINDUNILVR", "Hindustan Unilever Ltd", "Consumer Goods", "NSE"}
        };

        double[] indianStartPrices = {
            2450.0, 3600.0, 1480.0, 1650.0, 940.0,
            430.0, 870.0, 620.0, 410.0, 630.0,
            1250.0, 1120.0, 1780.0, 10500.0, 2400.0,
            7200.0, 5400.0, 3200.0, 1050.0, 2500.0
        };

        double[] indianVolatilities = {
            0.018, 0.015, 0.017, 0.016, 0.018,
            0.014, 0.019, 0.020, 0.018, 0.025,
            0.016, 0.019, 0.017, 0.018, 0.032,
            0.022, 0.020, 0.019, 0.018, 0.013
        };

        Random rng = new Random(42); // Deterministic seed for reproducibility

        // Seed international stocks (USD volumes)
        for (int i = 0; i < intlStockData.length; i++) {
            Stock stock = new Stock(intlStockData[i][0], intlStockData[i][1], intlStockData[i][2], intlStockData[i][3]);
            stock = stockRepository.save(stock);

            List<StockPrice> prices = generatePriceHistory(stock, intlStartPrices[i], intlVolatilities[i], rng, false);
            savePricesBatch(prices);
            System.out.println("  -> Loaded " + prices.size() + " price records for " + stock.getSymbol());
        }

        // Seed Indian stocks (INR volumes ── higher range)
        for (int i = 0; i < indianStockData.length; i++) {
            Stock stock = new Stock(indianStockData[i][0], indianStockData[i][1], indianStockData[i][2], indianStockData[i][3]);
            stock = stockRepository.save(stock);

            List<StockPrice> prices = generatePriceHistory(stock, indianStartPrices[i], indianVolatilities[i], rng, true);
            savePricesBatch(prices);
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

        System.out.println("[QuantTrade] Database seeding complete! Loaded 28 stocks.");
    }

    /**
     * Efficiently saves historical stock prices in a single raw JDBC batch update.
     */
    private void savePricesBatch(List<StockPrice> prices) {
        String sql = "INSERT INTO stock_prices (stock_id, date, open_price, high_price, low_price, close_price, volume) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                StockPrice price = prices.get(i);
                ps.setLong(1, price.getStock().getId());
                ps.setDate(2, Date.valueOf(price.getDate()));
                ps.setDouble(3, price.getOpenPrice());
                ps.setDouble(4, price.getHighPrice());
                ps.setDouble(5, price.getLowPrice());
                ps.setDouble(6, price.getClosePrice());
                ps.setLong(7, price.getVolume());
            }

            @Override
            public int getBatchSize() {
                return prices.size();
            }
        });
    }

    /**
     * Generates ~2 years of daily OHLCV price history using a random walk model.
     */
    private List<StockPrice> generatePriceHistory(Stock stock, double startPrice, double volatility, Random rng, boolean isIndian) {
        List<StockPrice> prices = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        double price = startPrice;

        long volumeBase = isIndian ? 5_500_000L : 5_000_000L;
        long volumeSpread = isIndian ? 2_500_000L : 2_000_000L;
        long volumeFloor = isIndian ? 1_000_000L : 500_000L;
        long volumeCeiling = isIndian ? 10_000_000L : Long.MAX_VALUE;

        while (!date.isAfter(endDate)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                double dailyReturn = rng.nextGaussian() * volatility;
                double open = price;
                double close = price * (1 + dailyReturn);
                double high = Math.max(open, close) * (1 + Math.abs(rng.nextGaussian() * volatility * 0.5));
                double low = Math.min(open, close) * (1 - Math.abs(rng.nextGaussian() * volatility * 0.5));
                long volume = (long) (volumeBase + rng.nextGaussian() * volumeSpread);
                if (volume < volumeFloor) volume = volumeFloor;
                if (volume > volumeCeiling) volume = volumeCeiling;

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
