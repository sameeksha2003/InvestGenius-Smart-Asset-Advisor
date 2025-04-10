package com.smartassetadvisor.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MarketSentimentService {

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MarketSentimentService.class);

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    @Value("${flask.api.url}")  // Flask Sentiment Analysis API
    private String flaskApiUrl;

    public MarketSentimentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches financial news from Alpha Vantage and analyzes sentiment using Flask AI API.
     * @return Market sentiment analysis results.
     */
    public Map<String, Object> fetchMarketNewsAndAnalyzeSentiment() {
        String url = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&apikey=" + apiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    
        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.error("Failed to fetch market news: HTTP {}", response.getStatusCode());
            return Collections.singletonMap("error", "Failed to fetch market news.");
        }
    
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray newsArray = jsonResponse.optJSONArray("feed");
    
        if (newsArray == null || newsArray.isEmpty()) {
            logger.warn("No market news found.");
            return Collections.singletonMap("message", "No market news available.");
        }
    
        int positive = 0, neutral = 0, negative = 0;
        List<String> headlines = new ArrayList<>();
    
        for (int i = 0; i < Math.min(newsArray.length(), 5); i++) { // Limit to 5 articles
            JSONObject article = newsArray.getJSONObject(i);
            String headline = article.optString("title", "No Title");
    
            // Send headline to Flask API for sentiment analysis
            String sentiment = analyzeSentiment(headline);
            headlines.add(headline);
    
            // Count sentiment distribution
            switch (sentiment) {
                case "positive":
                    positive++;
                    break;
                case "negative":
                    negative++;
                    break;
                case "neutral":
                    neutral++;
                    break;
            }
        }
    
        // Determine overall market mood
        String marketMood;
        if (positive > negative) marketMood = "📈 Positive";
        else if (negative > positive) marketMood = "📉 Negative";
        else marketMood = "📊 Neutral";
    
        // Generate a market summary based on sentiment
        String summary = generateMarketSummary(positive, negative, neutral, headlines);
    
        // Return market sentiment results
        Map<String, Object> result = new HashMap<>();
        result.put("marketMood", marketMood);
        result.put("summary", summary);
        return result;
    }
    
    /**
     * Generates a short market news summary.
     */
    private String generateMarketSummary(int positive, int negative, int neutral, List<String> headlines) {
        String trend = (positive > negative) ? "Optimistic outlook in the market." :
                       (negative > positive) ? "Investors are cautious due to negative trends." :
                       "Mixed market signals detected.";
    
        StringBuilder summary = new StringBuilder(trend + " Recent highlights:\n");
    
        for (String headline : headlines) {
            summary.append("• ").append(headline).append("\n");
        }
    
        return summary.toString();
    }
    

    /**
     * Sends text to Flask Sentiment Analysis API and returns sentiment result.
     * @param text 
     * @return 
     */
    private String analyzeSentiment(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(Collections.singletonMap("text", text), headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskApiUrl, request, Map.class);
            return (String) response.getBody().get("sentiment");
        } catch (Exception e) {
            logger.error("Sentiment analysis failed for text: {}. Error: {}", text, e.getMessage());
            return "unknown";
        }
    }
}
