package com.quanttrade.service;

import com.quanttrade.model.*;
import java.util.*;
import java.time.temporal.ChronoUnit;

public class BacktestEngine {

    private final List<StockPrice> prices;
    private final Strategy strategy;
    private final double initialCapital;
    private final Backtest backtest;

    public BacktestEngine(List<StockPrice> prices, Strategy strategy, double initialCapital, Backtest backtest) {
        this.prices = prices;
        this.strategy = strategy;
        this.initialCapital = initialCapital;
        this.backtest = backtest;
    }

    public static class EngineResult {
        public BacktestResult result;
        public List<Trade> trades;
        public List<Double> equityCurve;
    }

    public EngineResult execute() {
        switch (strategy.getType()) {
            case "SMA_CROSSOVER": return executeSMA();
            case "RSI": return executeRSI();
            case "MACD": return executeMACD();
            case "BOLLINGER_BANDS": return executeBollinger();
            default: throw new IllegalArgumentException("Unknown strategy type: " + strategy.getType());
        }
    }

    // ========== SMA CROSSOVER ==========
    private EngineResult executeSMA() {
        StrategyIndicator ind = strategy.getIndicators().get(0);
        int shortPeriod = ind.getPeriod1();
        int longPeriod = ind.getPeriod2();

        double[] closes = prices.stream().mapToDouble(StockPrice::getClosePrice).toArray();
        double[] shortSMA = computeSMA(closes, shortPeriod);
        double[] longSMA = computeSMA(closes, longPeriod);

        List<Trade> trades = new ArrayList<>();
        List<Double> equityCurve = new ArrayList<>();
        double cash = initialCapital;
        int shares = 0;
        double buyPrice = 0;

        for (int i = longPeriod; i < closes.length; i++) {
            double portfolioValue = cash + shares * closes[i];
            equityCurve.add(portfolioValue);

            // BUY signal: short SMA crosses above long SMA
            if (shares == 0 && shortSMA[i] > longSMA[i] && shortSMA[i - 1] <= longSMA[i - 1]) {
                shares = (int) (cash / closes[i]);
                if (shares > 0) {
                    buyPrice = closes[i];
                    cash -= shares * closes[i];
                    trades.add(new Trade(backtest, "BUY", prices.get(i).getDate(), closes[i], shares, null, cash + shares * closes[i], "SMA Cross Up"));
                }
            }
            // SELL signal: short SMA crosses below long SMA
            else if (shares > 0 && shortSMA[i] < longSMA[i] && shortSMA[i - 1] >= longSMA[i - 1]) {
                double pnl = (closes[i] - buyPrice) * shares;
                cash += shares * closes[i];
                trades.add(new Trade(backtest, "SELL", prices.get(i).getDate(), closes[i], shares, pnl, cash, "SMA Cross Down"));
                shares = 0;
            }
        }

        // Close any open position at end
        if (shares > 0) {
            double lastPrice = closes[closes.length - 1];
            double pnl = (lastPrice - buyPrice) * shares;
            cash += shares * lastPrice;
            trades.add(new Trade(backtest, "SELL", prices.get(prices.size() - 1).getDate(), lastPrice, shares, pnl, cash, "End of Period"));
            shares = 0;
        }

        return buildResult(trades, equityCurve, cash);
    }

    // ========== RSI ==========
    private EngineResult executeRSI() {
        StrategyIndicator ind = strategy.getIndicators().get(0);
        int period = ind.getPeriod1();
        double oversold = ind.getThreshold1();
        double overbought = ind.getThreshold2();

        double[] closes = prices.stream().mapToDouble(StockPrice::getClosePrice).toArray();
        double[] rsi = computeRSI(closes, period);

        List<Trade> trades = new ArrayList<>();
        List<Double> equityCurve = new ArrayList<>();
        double cash = initialCapital;
        int shares = 0;
        double buyPrice = 0;

        for (int i = period + 1; i < closes.length; i++) {
            double portfolioValue = cash + shares * closes[i];
            equityCurve.add(portfolioValue);

            if (shares == 0 && rsi[i] < oversold && rsi[i - 1] >= oversold) {
                shares = (int) (cash / closes[i]);
                if (shares > 0) {
                    buyPrice = closes[i];
                    cash -= shares * closes[i];
                    trades.add(new Trade(backtest, "BUY", prices.get(i).getDate(), closes[i], shares, null, cash + shares * closes[i], "RSI Oversold (" + String.format("%.1f", rsi[i]) + ")"));
                }
            } else if (shares > 0 && rsi[i] > overbought && rsi[i - 1] <= overbought) {
                double pnl = (closes[i] - buyPrice) * shares;
                cash += shares * closes[i];
                trades.add(new Trade(backtest, "SELL", prices.get(i).getDate(), closes[i], shares, pnl, cash, "RSI Overbought (" + String.format("%.1f", rsi[i]) + ")"));
                shares = 0;
            }
        }

        if (shares > 0) {
            double lastPrice = closes[closes.length - 1];
            double pnl = (lastPrice - buyPrice) * shares;
            cash += shares * lastPrice;
            trades.add(new Trade(backtest, "SELL", prices.get(prices.size() - 1).getDate(), lastPrice, shares, pnl, cash, "End of Period"));
        }

        return buildResult(trades, equityCurve, cash);
    }

    // ========== MACD ==========
    private EngineResult executeMACD() {
        StrategyIndicator ind = strategy.getIndicators().get(0);
        int fastPeriod = ind.getPeriod1();  // 12
        int slowPeriod = ind.getPeriod2();  // 26
        int signalPeriod = ind.getThreshold1().intValue(); // 9

        double[] closes = prices.stream().mapToDouble(StockPrice::getClosePrice).toArray();
        double[] fastEMA = computeEMA(closes, fastPeriod);
        double[] slowEMA = computeEMA(closes, slowPeriod);
        double[] macdLine = new double[closes.length];
        for (int i = 0; i < closes.length; i++) {
            macdLine[i] = fastEMA[i] - slowEMA[i];
        }
        double[] signalLine = computeEMA(macdLine, signalPeriod);

        List<Trade> trades = new ArrayList<>();
        List<Double> equityCurve = new ArrayList<>();
        double cash = initialCapital;
        int shares = 0;
        double buyPrice = 0;
        int start = slowPeriod + signalPeriod;

        for (int i = start; i < closes.length; i++) {
            double portfolioValue = cash + shares * closes[i];
            equityCurve.add(portfolioValue);

            if (shares == 0 && macdLine[i] > signalLine[i] && macdLine[i - 1] <= signalLine[i - 1]) {
                shares = (int) (cash / closes[i]);
                if (shares > 0) {
                    buyPrice = closes[i];
                    cash -= shares * closes[i];
                    trades.add(new Trade(backtest, "BUY", prices.get(i).getDate(), closes[i], shares, null, cash + shares * closes[i], "MACD Cross Up"));
                }
            } else if (shares > 0 && macdLine[i] < signalLine[i] && macdLine[i - 1] >= signalLine[i - 1]) {
                double pnl = (closes[i] - buyPrice) * shares;
                cash += shares * closes[i];
                trades.add(new Trade(backtest, "SELL", prices.get(i).getDate(), closes[i], shares, pnl, cash, "MACD Cross Down"));
                shares = 0;
            }
        }

        if (shares > 0) {
            double lastPrice = closes[closes.length - 1];
            double pnl = (lastPrice - buyPrice) * shares;
            cash += shares * lastPrice;
            trades.add(new Trade(backtest, "SELL", prices.get(prices.size() - 1).getDate(), lastPrice, shares, pnl, cash, "End of Period"));
        }

        return buildResult(trades, equityCurve, cash);
    }

    // ========== BOLLINGER BANDS ==========
    private EngineResult executeBollinger() {
        StrategyIndicator ind = strategy.getIndicators().get(0);
        int period = ind.getPeriod1();
        double numStdDev = ind.getThreshold1();

        double[] closes = prices.stream().mapToDouble(StockPrice::getClosePrice).toArray();
        double[] sma = computeSMA(closes, period);

        List<Trade> trades = new ArrayList<>();
        List<Double> equityCurve = new ArrayList<>();
        double cash = initialCapital;
        int shares = 0;
        double buyPrice = 0;

        for (int i = period; i < closes.length; i++) {
            double stdDev = computeStdDev(closes, i, period);
            double upperBand = sma[i] + numStdDev * stdDev;
            double lowerBand = sma[i] - numStdDev * stdDev;

            double portfolioValue = cash + shares * closes[i];
            equityCurve.add(portfolioValue);

            if (shares == 0 && closes[i] <= lowerBand) {
                shares = (int) (cash / closes[i]);
                if (shares > 0) {
                    buyPrice = closes[i];
                    cash -= shares * closes[i];
                    trades.add(new Trade(backtest, "BUY", prices.get(i).getDate(), closes[i], shares, null, cash + shares * closes[i], "Price hit Lower Band"));
                }
            } else if (shares > 0 && closes[i] >= upperBand) {
                double pnl = (closes[i] - buyPrice) * shares;
                cash += shares * closes[i];
                trades.add(new Trade(backtest, "SELL", prices.get(i).getDate(), closes[i], shares, pnl, cash, "Price hit Upper Band"));
                shares = 0;
            }
        }

        if (shares > 0) {
            double lastPrice = closes[closes.length - 1];
            double pnl = (lastPrice - buyPrice) * shares;
            cash += shares * lastPrice;
            trades.add(new Trade(backtest, "SELL", prices.get(prices.size() - 1).getDate(), lastPrice, shares, pnl, cash, "End of Period"));
        }

        return buildResult(trades, equityCurve, cash);
    }

    // ========== RESULT BUILDER ==========
    private EngineResult buildResult(List<Trade> trades, List<Double> equityCurve, double finalCash) {
        BacktestResult result = new BacktestResult();
        result.setBacktest(backtest);
        result.setFinalCapital(Math.round(finalCash * 100.0) / 100.0);
        result.setTotalReturn(Math.round(((finalCash - initialCapital) / initialCapital) * 10000.0) / 100.0);

        // Calculate CAGR
        long days = ChronoUnit.DAYS.between(backtest.getStartDate(), backtest.getEndDate());
        double years = days / 365.25;
        if (years > 0 && finalCash > 0) {
            result.setCagr(Math.round((Math.pow(finalCash / initialCapital, 1.0 / years) - 1) * 10000.0) / 100.0);
        } else {
            result.setCagr(0.0);
        }

        // Analyze trades
        List<Double> pnls = new ArrayList<>();
        double totalWins = 0, totalLosses = 0;
        int wins = 0, losses = 0;
        double largestWin = Double.MIN_VALUE, largestLoss = Double.MAX_VALUE;

        for (Trade t : trades) {
            if ("SELL".equals(t.getType()) && t.getPnl() != null) {
                pnls.add(t.getPnl());
                if (t.getPnl() > 0) {
                    wins++;
                    totalWins += t.getPnl();
                    largestWin = Math.max(largestWin, t.getPnl());
                } else {
                    losses++;
                    totalLosses += Math.abs(t.getPnl());
                    largestLoss = Math.min(largestLoss, t.getPnl());
                }
            }
        }

        int totalTrades = wins + losses;
        result.setTotalTrades(totalTrades);
        result.setProfitableTrades(wins);
        result.setLosingTrades(losses);
        result.setWinRate(totalTrades > 0 ? Math.round((double) wins / totalTrades * 10000.0) / 100.0 : 0.0);
        result.setAvgWin(wins > 0 ? Math.round(totalWins / wins * 100.0) / 100.0 : 0.0);
        result.setAvgLoss(losses > 0 ? Math.round(totalLosses / losses * 100.0) / 100.0 : 0.0);
        result.setLargestWin(largestWin != Double.MIN_VALUE ? Math.round(largestWin * 100.0) / 100.0 : 0.0);
        result.setLargestLoss(largestLoss != Double.MAX_VALUE ? Math.round(largestLoss * 100.0) / 100.0 : 0.0);
        result.setProfitFactor(totalLosses > 0 ? Math.round(totalWins / totalLosses * 100.0) / 100.0 : totalWins > 0 ? 999.99 : 0.0);

        // Sharpe Ratio (using daily returns from equity curve)
        if (equityCurve.size() > 1) {
            double[] returns = new double[equityCurve.size() - 1];
            for (int i = 1; i < equityCurve.size(); i++) {
                returns[i - 1] = (equityCurve.get(i) - equityCurve.get(i - 1)) / equityCurve.get(i - 1);
            }
            double avgReturn = Arrays.stream(returns).average().orElse(0);
            double stdDev = Math.sqrt(Arrays.stream(returns).map(r -> Math.pow(r - avgReturn, 2)).average().orElse(0));
            result.setSharpeRatio(stdDev > 0 ? Math.round((avgReturn / stdDev) * Math.sqrt(252) * 100.0) / 100.0 : 0.0);
        } else {
            result.setSharpeRatio(0.0);
        }

        // Max Drawdown
        double maxDrawdown = 0;
        double peak = 0;
        for (Double equity : equityCurve) {
            if (equity > peak) peak = equity;
            double drawdown = (peak - equity) / peak * 100;
            if (drawdown > maxDrawdown) maxDrawdown = drawdown;
        }
        result.setMaxDrawdown(Math.round(maxDrawdown * 100.0) / 100.0);

        EngineResult engineResult = new EngineResult();
        engineResult.result = result;
        engineResult.trades = trades;
        engineResult.equityCurve = equityCurve;
        return engineResult;
    }

    // ========== INDICATOR COMPUTATIONS ==========
    private double[] computeSMA(double[] data, int period) {
        double[] sma = new double[data.length];
        for (int i = period - 1; i < data.length; i++) {
            double sum = 0;
            for (int j = i - period + 1; j <= i; j++) sum += data[j];
            sma[i] = sum / period;
        }
        return sma;
    }

    private double[] computeEMA(double[] data, int period) {
        double[] ema = new double[data.length];
        double multiplier = 2.0 / (period + 1);
        ema[0] = data[0];
        for (int i = 1; i < data.length; i++) {
            ema[i] = (data[i] - ema[i - 1]) * multiplier + ema[i - 1];
        }
        return ema;
    }

    private double[] computeRSI(double[] closes, int period) {
        double[] rsi = new double[closes.length];
        double[] gains = new double[closes.length];
        double[] losses = new double[closes.length];

        for (int i = 1; i < closes.length; i++) {
            double change = closes[i] - closes[i - 1];
            gains[i] = change > 0 ? change : 0;
            losses[i] = change < 0 ? -change : 0;
        }

        double avgGain = 0, avgLoss = 0;
        for (int i = 1; i <= period; i++) {
            avgGain += gains[i];
            avgLoss += losses[i];
        }
        avgGain /= period;
        avgLoss /= period;

        rsi[period] = avgLoss == 0 ? 100 : 100 - (100 / (1 + avgGain / avgLoss));

        for (int i = period + 1; i < closes.length; i++) {
            avgGain = (avgGain * (period - 1) + gains[i]) / period;
            avgLoss = (avgLoss * (period - 1) + losses[i]) / period;
            rsi[i] = avgLoss == 0 ? 100 : 100 - (100 / (1 + avgGain / avgLoss));
        }

        return rsi;
    }

    private double computeStdDev(double[] data, int endIndex, int period) {
        double sum = 0;
        for (int i = endIndex - period + 1; i <= endIndex; i++) sum += data[i];
        double mean = sum / period;
        double variance = 0;
        for (int i = endIndex - period + 1; i <= endIndex; i++) variance += Math.pow(data[i] - mean, 2);
        return Math.sqrt(variance / period);
    }
}
