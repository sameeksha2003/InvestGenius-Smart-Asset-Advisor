package com.smartassetadvisor.controller;

import com.smartassetadvisor.dto.StockData;
import com.smartassetadvisor.service.StockService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/{symbol}")
    public StockData getStockPrice(@PathVariable String symbol) {
        return stockService.getStockPrice(symbol);
    }
}
