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
            stockPriceRepository.saveAll(prices);
            System.out.println("  -> Loaded " + prices.size() + " price records for " + stock.getSymbol());
        }

        // Seed Indian stocks (INR volumes — higher range)
        for (int i = 0; i < indianStockData.length; i++) {
            Stock stock = new Stock(indianStockData[i][0], indianStockData[i][1], indianStockData[i][2], indianStockData[i][3]);
            stock = stockRepository.save(stock);

            List<StockPrice> prices = generatePriceHistory(stock, indianStartPrices[i], indianVolatilities[i], rng, true);
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

        System.out.println("[QuantTrade] Database seeding complete! Loaded " + (intlStockData.length + indianStockData.length) + " stocks.");
    }

    /**
     * Generates ~2 years of daily OHLCV price history using a random walk model.
     *
     * @param stock      the stock entity
     * @param startPrice initial price
     * @param volatility daily volatility factor
     * @param rng        seeded Random instance
     * @param isIndian   if true, uses higher volume range typical of NSE (1M–10M)
     */
    private List<StockPrice> generatePriceHistory(Stock stock, double startPrice, double volatility, Random rng, boolean isIndian) {
        List<StockPrice> prices = new ArrayList<>();
        LocalDate date = LocalDate.of(2023, 1, 2);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        double price = startPrice;

        // Volume parameters differ between international and Indian stocks
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
