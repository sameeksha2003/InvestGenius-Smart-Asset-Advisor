package com.smartassetadvisor.service;

import com.smartassetadvisor.model.MarketMood;
import com.smartassetadvisor.repository.MarketMoodRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;

@Service
public class MarketMoodService {

    private final MarketMoodRepository marketMoodRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MarketMoodService.class);

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    public MarketMoodService(MarketMoodRepository marketMoodRepository, RestTemplate restTemplate) {
        this.marketMoodRepository = marketMoodRepository;
        this.restTemplate = restTemplate;
    }

    public String fetchAndStoreMarketMood() {
        String url = "https://www.alphavantage.co/query?function=MARKET_STATUS&apikey=" + apiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to fetch market data: HTTP {}", response.getStatusCode());
            return "Failed to fetch market data.";
        }

        logger.info("Response from Alpha Vantage: {}", response.getBody());

        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray markets = jsonResponse.optJSONArray("markets");

        if (markets == null || markets.length() == 0) {
            logger.warn("No market data found.");
            return "No market data available.";
        }

        marketMoodRepository.deleteAll();
        logger.info("Old market mood data cleared.");

        for (int i = 0; i < markets.length(); i++) {
            JSONObject market = markets.getJSONObject(i);

            MarketMood mood = new MarketMood();
            mood.setRegion(market.optString("region", "Unknown"));
            mood.setMarketType(market.optString("market_type", "General"));
            mood.setPrimaryExchanges(market.optString("primary_exchanges", "N/A"));

            try {
                mood.setLocalOpen(LocalTime.parse(market.optString("local_open", "00:00")));
                mood.setLocalClose(LocalTime.parse(market.optString("local_close", "23:59")));
            } catch (Exception e) {
                logger.error("Error parsing time for {}: {}", mood.getRegion(), e.getMessage());
                continue;
            }

            mood.setCurrentStatus(market.optString("current_status", "Unknown"));
            mood.setNotes(market.optString("notes", ""));

            marketMoodRepository.save(mood);
            logger.info("Saved market data: {}", mood);
        }

        return "Market mood data updated successfully.";
    }

    public List<MarketMood> getMarketMood() {
        return marketMoodRepository.findAll();
    }
}
