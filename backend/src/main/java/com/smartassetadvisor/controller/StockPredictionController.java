package com.smartassetadvisor.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/stock-predictions")
@CrossOrigin(origins = "*")
public class StockPredictionController {

    @Value("${ml.api.url:http://localhost:5001/predict-lstm-batch}")
    private String mlApiUrl;

    @GetMapping
    public ResponseEntity<String> getLstmPrediction() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(mlApiUrl, String.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error contacting ML service");
        }
    }

    @GetMapping("/image")
    public ResponseEntity<String> getPredictionImageUrl() {
        return ResponseEntity.ok("http://localhost:5001/plot/lstm_plot.png");
    }
}
