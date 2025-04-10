package com.smartassetadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InvestmentRecommendation {
    private String assetType;
    private String description;
    private double recommendedPercentage;
}
