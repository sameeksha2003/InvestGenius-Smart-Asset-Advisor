package com.smartassetadvisor.service;

import com.smartassetadvisor.model.User;
import com.smartassetadvisor.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

@Service
public class InvestmentService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public InvestmentService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public String getInvestmentRecommendations(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "❌ User not found. Please update your profile.";
        }

        User user = userOptional.get();
        String riskCategory = user.getRiskCategory();

        return provideDetailedInvestmentPlan(riskCategory);
    }

    private String provideDetailedInvestmentPlan(String riskCategory) {
        StringBuilder recommendation = new StringBuilder();
        recommendation.append("📊 **Investment Plan Based on Your Risk Profile**\n\n");

        switch (riskCategory.toLowerCase()) {
            case "low":
                recommendation.append("🔵 **Conservative Investor:**\n")
                              .append("You prefer **low risk** and **stable returns**. Focus on **safe investments** that protect your capital.\n\n")
                              .append("💡 **Recommended Investments:**\n")
                              .append("✅ Government Bonds (US Treasury, Municipal Bonds)\n")
                              .append("✅ Fixed Deposits & High-Yield Savings\n")
                              .append("✅ Dividend Stocks & Blue-Chip Stocks\n")
                              .append("✅ Real Estate Investment Trusts (REITs)\n");
                break;

            case "medium":
                recommendation.append("📈 **Balanced Investor:**\n")
                              .append("You seek **moderate risk with steady growth**. Diversify between **stocks, bonds, and alternative investments**.\n\n")
                              .append("💡 **Recommended Investments:**\n")
                              .append("✅ Exchange Traded Funds (ETFs) & Mutual Funds\n")
                              .append("✅ Large Cap Stocks (Apple, Microsoft, Google)\n")
                              .append("✅ 60% Stocks, 30% Bonds, 10% Alternatives\n")
                              .append("✅ Real Estate & Commodities\n");
                break;

            case "high":
                recommendation.append("🔥 **Aggressive Investor:**\n")
                              .append("You're willing to take **high risks for high rewards**. Focus on **growth stocks, crypto, and options trading**.\n\n")
                              .append("💡 **Recommended Investments:**\n")
                              .append("✅ Small Cap & Tech Growth Stocks (Tesla, NVIDIA, AI Stocks)\n")
                              .append("✅ Cryptocurrencies (Bitcoin, Ethereum, Altcoins)\n")
                              .append("✅ Options Trading & High-Yield Bonds\n")
                              .append("✅ Startups & Venture Capital\n");
                break;

            default:
                recommendation.append("🚀 **General Investment Strategy:**\n")
                              .append("A diversified portfolio **protects against market fluctuations** and **maximizes returns**.\n\n")
                              .append("💡 **Recommended: A mix of Stocks, Bonds, and Alternative Assets**.");
        }

        return recommendation.toString();
    }

    public String getStockMarketData(String symbol) {
        String apiKey = "YOUR_ALPHA_VANTAGE_API_KEY";
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=5min&apikey=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }
}
