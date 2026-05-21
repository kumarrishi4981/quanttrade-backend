package com.quanttrade.model;

import jakarta.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String symbol;

    @Column(nullable = false)
    private String name;

    private String sector;
    private String exchange;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<StockPrice> prices;

    public Stock() {}

    public Stock(String symbol, String name, String sector, String exchange) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.exchange = exchange;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
    public List<StockPrice> getPrices() { return prices; }
    public void setPrices(List<StockPrice> prices) { this.prices = prices; }
}
