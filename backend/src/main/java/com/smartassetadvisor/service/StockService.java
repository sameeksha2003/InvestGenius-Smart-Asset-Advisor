package com.smartassetadvisor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartassetadvisor.dto.StockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockService {

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    @Value("${alpha.vantage.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public StockData getStockPrice(String symbol) {
        String url = baseUrl + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
    
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response); 
            JsonNode globalQuote = jsonResponse.get("Global Quote");
    
            if (globalQuote == null) {
                throw new RuntimeException("Invalid response: 'Global Quote' field is missing.");
            }
    
            String stockSymbol = globalQuote.get("01. symbol").asText();
            double price = globalQuote.get("05. price").asDouble();
            double change = globalQuote.get("09. change").asDouble();
    
            // Fix: Ensure conversion works correctly
            String percentChangeStr = globalQuote.get("10. change percent").asText();
            double percentChange = Double.parseDouble(percentChangeStr.replace("%", ""));
    
            return new StockData(stockSymbol, price, change, percentChange);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse JSON response: " + e.getMessage(), e);
        }
    }
}    
