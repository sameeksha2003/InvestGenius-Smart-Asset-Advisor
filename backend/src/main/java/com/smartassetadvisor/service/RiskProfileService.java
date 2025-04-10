package com.smartassetadvisor.service;

import org.springframework.stereotype.Service;

@Service
public class RiskProfileService {

    public String calculateRisk(int age, String occupation, double annualIncome, String investmentGoal) {
        int riskScore = 0;
    
        if (age < 30) {
            riskScore += 2;
        } else if (age < 50) {
            riskScore += 1;
        }
    
        if (occupation.equalsIgnoreCase("Business")) {
            riskScore += 2;
        } else if (occupation.equalsIgnoreCase("Private") || occupation.equalsIgnoreCase("Govt")) {
            riskScore += 1;
        }
        if (annualIncome < 100000) {
            riskScore += 0;
        } else if (annualIncome <= 500000) {
            riskScore += 1;
        } else if (annualIncome <= 1000000) {
            riskScore += 2;
        } else {
            riskScore += 3;
        }

        if (investmentGoal.equalsIgnoreCase("Retirement") || investmentGoal.equalsIgnoreCase("House")) {
            riskScore -= 1;
        }
    
        if (riskScore >= 4) {
            return "High";
        } else if (riskScore >= 2) {
            return "Medium";
        } else {
            return "Low";
        }
    }
    
}
