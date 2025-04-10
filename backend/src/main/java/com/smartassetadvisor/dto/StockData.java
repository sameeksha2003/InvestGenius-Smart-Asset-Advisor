package com.smartassetadvisor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockData {
    private String symbol;
    private double price;
    private double change;
    private double percentChange;

    public StockData(String symbol, double price, double change, double percentChange) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.percentChange = percentChange;
    }
}
