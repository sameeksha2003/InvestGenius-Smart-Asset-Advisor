package com.smartassetadvisor.controller;

import com.smartassetadvisor.model.MarketMood;
import com.smartassetadvisor.service.MarketMoodService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketMoodController {
    private final MarketMoodService marketMoodService;

    public MarketMoodController(MarketMoodService marketMoodService) {
        this.marketMoodService = marketMoodService;
    }

    @PostMapping("/fetch-market-mood")
    public ResponseEntity<String> fetchMarketMood() {
        String response = marketMoodService.fetchAndStoreMarketMood();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/market-mood")
    public ResponseEntity<List<MarketMood>> getMarketMood() {
        return ResponseEntity.ok(marketMoodService.getMarketMood());
    }
}

