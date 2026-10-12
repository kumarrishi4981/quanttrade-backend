# ⚙️ QuantTrade Backend — Quantitative Backtesting Engine

> The institutional-grade Java Spring Boot execution engine powering QuantTrade. Implements algorithmic trading strategies, historical price simulations, mathematical indicator calculations, and portfolio risk telemetry.

[![Live API](https://img.shields.io/badge/API-Live_Deployment-success?style=for-the-badge&logo=render&logoColor=white)](https://quanttrade-api.onrender.com/)
[![Frontend App](https://img.shields.io/badge/Frontend-quanttrade--frontend-blue?style=for-the-badge&logo=vercel)](https://quanttrade-frontend.vercel.app/)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Data JPA](https://img.shields.io/badge/Spring_Data-JPA-blue?style=for-the-badge)](https://spring.io/projects/spring-data-jpa)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

---

### 🌐 Live Deployments
- **REST API Base URL**: [https://quanttrade-api.onrender.com/](https://quanttrade-api.onrender.com/)
- **Web Client UI**: [https://quanttrade-frontend.vercel.app/](https://quanttrade-frontend.vercel.app/)

---

## 📌 Overview

**QuantTrade Backend** is the analytical backbone for the QuantTrade algorithmic simulation platform. Built with **Spring Boot 3** and **Java 17**, it models historical market behaviors across 28 Indian (NSE/BSE) and international tickers.

At the core of the service is `BacktestEngine.java`—a deterministic simulation engine that iterates across historical price candles, evaluates indicator threshold crossings, simulates buy/sell execution logic, tracks portfolio cash vs. asset equity, and outputs key risk metrics (CAGR, Max Drawdown, Sharpe Ratio, and Win Rate).

---

## 📐 Algorithmic Strategies & Mathematical Indicators

The engine features native mathematical implementations of the most widely used quantitative strategies:

### 1. Simple Moving Average Crossover (`SMA_CROSSOVER`)
* **Logic**: Evaluates a short-window moving average ($SMA_{fast}$) against a long-window moving average ($SMA_{slow}$).
* **Signal**:
  - **BUY**: When $SMA_{fast}$ crosses strictly above $SMA_{slow}$.
  - **SELL**: When $SMA_{fast}$ crosses below $SMA_{slow}$.

### 2. Relative Strength Index (`RSI`)
* **Logic**: Measures momentum by computing the ratio of average exponential gains to average losses over period $N$ (default 14).
* **Signal**:
  - **BUY**: When RSI dips into oversold territory ($\le 30$) and reverses upwards.
  - **SELL**: When RSI crosses into overbought territory ($\ge 70$).

### 3. Moving Average Convergence Divergence (`MACD`)
* **Logic**: Computes the difference between 12-period EMA and 26-period EMA, evaluated against a 9-period Signal line.
* **Signal**:
  - **BUY**: Bullish MACD line crossover above the Signal line.
  - **SELL**: Bearish MACD line crossover below the Signal line.

### 4. Bollinger Bands (`BOLLINGER_BANDS`)
* **Logic**: 20-period simple moving average flanked by upper and lower bands positioned $K \times \sigma$ standard deviations away.
* **Signal**: Mean-reversion entries on lower-band touches and exits on upper-band extensions.

---

## 📊 Performance & Risk Metrics Computed

For every backtest, the engine calculates:
* **Total Return (%)**: Absolute percentage growth of the portfolio.
* **CAGR**: Compound Annual Growth Rate over the simulated date duration.
* **Max Drawdown (MDD %)**: The greatest peak-to-trough decline before a new peak is achieved.
* **Sharpe Ratio**: Risk-adjusted excess return over annualized volatility.
* **Win Rate (%)**: Ratio of profitable closed positions to total closed trades.
* **Equity Curve**: Time-series equity balance calculated after every transaction.
* **Complete Trade Logs**: Entry date/price, exit date/price, shares, and P&L for every order.

---

## 🛠️ Architecture & System Design

```text
┌─────────────────────────────────┐
│     Client (React / Vercel)     │
└────────────────┬────────────────┘
                 │ HTTP REST / JSON
                 ▼
┌─────────────────────────────────┐
│     Spring Boot Controllers     │  (/api/backtests, /api/stocks, /api/strategies)
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│       BacktestService           │
└────────────────┬────────────────┘
                 │ Passes Historical Prices & Parameters
                 ▼
┌─────────────────────────────────┐
│       BacktestEngine            │  (Calculates SMA/RSI/MACD/BB, Portfolio Value, Trades)
└────────────────┬────────────────┘
                 │
                 ▼
┌─────────────────────────────────┐
│    Spring Data JPA / DB Layer   │  (H2 / PostgreSQL Persistence)
└─────────────────────────────────┘
```

---

## 📂 Project Structure

```text
quanttrade-backend/
├── src/main/java/com/quanttrade/
│   ├── config/                  # CORS filter (CorsConfig.java) & Data seeder (DataLoader.java)
│   ├── controller/              # REST API controllers
│   │   ├── BacktestController.java   # /api/backtests execution & logs
│   │   ├── DashboardController.java  # /api/dashboard summary metrics
│   │   ├── StockController.java      # /api/stocks directory & historical data
│   │   ├── StrategyController.java   # /api/strategies indicator catalog
│   │   └── HomeController.java       # Health probe
│   ├── dto/                     # Request and Response transfer models
│   ├── model/                   # JPA Entities (Stock, StockPrice, Strategy, Backtest, Trade)
│   ├── repository/              # Spring Data JPA CRUD & Query interfaces
│   ├── service/                 # Business logic & BacktestEngine execution
│   └── QuantTradeApplication.java # Spring Boot entrypoint
├── src/main/resources/
│   ├── application.properties   # Local development config (H2 in-memory)
│   └── application-prod.properties # Cloud deployment config
├── Dockerfile                   # Multi-stage Docker container build
├── render.yaml                  # Infrastructure-as-code for Render deployment
└── pom.xml                      # Maven dependencies
```

---

## 📡 REST API Reference

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/` | Health check probe & root information |
| `GET` | `/api/dashboard` | Platform metrics (total stocks, active strategies, backtests count) |
| `GET` | `/api/stocks` | Lists all 28 supported Indian & global equities |
| `GET` | `/api/stocks/{id}` | Asset details and metadata |
| `GET` | `/api/stocks/{id}/prices` | Historical price candles (Open, High, Low, Close, Volume) |
| `GET` | `/api/strategies` | Catalog of pre-configured quantitative strategies |
| `POST` | `/api/strategies` | Create and save a custom technical indicator rule |
| `POST` | `/api/backtests/run` | Execute a backtest simulation on historical data |
| `GET` | `/api/backtests` | List all previous backtest runs |
| `GET` | `/api/backtests/{id}` | Retrieve comprehensive backtest results, equity curve & trade list |

---

## 🚀 Local Setup & Installation

### Prerequisites
- **Java Development Kit (JDK)** 17+
- **Maven** 3.8+ (or use the included `./mvnw` wrapper)

### 1. Clone the Repository
```bash
git clone https://github.com/kumarrishi4981/quanttrade-backend.git
cd quanttrade-backend
```

### 2. Run with Maven Wrapper
```bash
# On Linux / macOS:
./mvnw clean spring-boot:run

# On Windows:
.\mvnw.cmd clean spring-boot:run
```
The server will start on `http://localhost:8080`.

### 3. Verify Health
Visit `http://localhost:8080/` in your browser. You should receive:
```text
📈 QuantTrade REST API is up and running!
```

---

## ☁️ Deployment

The repository includes a ready-to-run `Dockerfile` and `render.yaml` blueprint:
- **Build**: `./mvnw clean package -DskipTests`
- **Run**: `java -jar target/quanttrade-backend-1.0.0.jar`
- Automatic dynamic port binding handles cloud port injection (`$PORT`).

---

## 🤝 Contributing
Contributions and new indicator implementations (e.g., ATR, VWAP, Supertrend) are welcome!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/VWAPIndicator`)
3. Commit your Changes (`git commit -m 'feat: Implement Volume Weighted Average Price'`)
4. Push to the Branch (`git push origin feature/VWAPIndicator`)
5. Open a Pull Request

---

## 📄 License
This project is open-source under the **MIT License**.

---

## 👤 Author
- **Rishi Kumar**
- GitHub: [@kumarrishi4981](https://github.com/kumarrishi4981)
- Live API: [QuantTrade on Render](https://quanttrade-api.onrender.com/)
