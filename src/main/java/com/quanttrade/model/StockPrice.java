package com.quanttrade.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "stock_prices", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"stock_id", "date"})
})
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    @JsonIgnore
    private Stock stock;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double openPrice;

    @Column(nullable = false)
    private Double highPrice;

    @Column(nullable = false)
    private Double lowPrice;

    @Column(nullable = false)
    private Double closePrice;

    @Column(nullable = false)
    private Long volume;

    public StockPrice() {}

    public StockPrice(Stock stock, LocalDate date, Double openPrice, Double highPrice, Double lowPrice, Double closePrice, Long volume) {
        this.stock = stock;
        this.date = date;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Stock getStock() { return stock; }
    public void setStock(Stock stock) { this.stock = stock; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public Double getOpenPrice() { return openPrice; }
    public void setOpenPrice(Double openPrice) { this.openPrice = openPrice; }
    public Double getHighPrice() { return highPrice; }
    public void setHighPrice(Double highPrice) { this.highPrice = highPrice; }
    public Double getLowPrice() { return lowPrice; }
    public void setLowPrice(Double lowPrice) { this.lowPrice = lowPrice; }
    public Double getClosePrice() { return closePrice; }
    public void setClosePrice(Double closePrice) { this.closePrice = closePrice; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
}
