package com.smartassetadvisor.controller;

import com.smartassetadvisor.service.MarketSentimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/market-sentiment")
public class MarketSentimentController {

    private final MarketSentimentService marketSentimentService;

    public MarketSentimentController(MarketSentimentService marketSentimentService) {
        this.marketSentimentService = marketSentimentService;
    }

    @GetMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeMarketSentiment() {
        Map<String, Object> response = marketSentimentService.fetchMarketNewsAndAnalyzeSentiment();
        return ResponseEntity.ok(response);
    }
}
