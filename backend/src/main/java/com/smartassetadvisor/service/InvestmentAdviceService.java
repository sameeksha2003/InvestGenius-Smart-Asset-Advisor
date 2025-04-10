package com.smartassetadvisor.service;

import com.smartassetadvisor.model.User;
import com.smartassetadvisor.model.MarketMood;
import com.smartassetadvisor.repository.UserRepository;
import com.smartassetadvisor.repository.MarketMoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InvestmentAdviceService {

    private final UserRepository userRepository;
    private final MarketMoodRepository marketMoodRepository;

    public InvestmentAdviceService(UserRepository userRepository, MarketMoodRepository marketMoodRepository) {
        this.userRepository = userRepository;
        this.marketMoodRepository = marketMoodRepository;
    }

    public String getInvestmentAdvice(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "<p style='color:red;'>❌ User profile not found. Please complete your profile for personalized investment advice.</p>";
        }

        User user = userOpt.get();
        String riskProfile = user.getRiskCategory();
        List<MarketMood> marketMoodList = marketMoodRepository.findAll();

        String marketStatus = analyzeMarketMood(marketMoodList);
        return generateDetailedAdvice(riskProfile, marketStatus);
    }

    private String analyzeMarketMood(List<MarketMood> marketMoodList) {
        long bullish = marketMoodList.stream().filter(m -> m.getCurrentStatus().equalsIgnoreCase("bullish")).count();
        long bearish = marketMoodList.stream().filter(m -> m.getCurrentStatus().equalsIgnoreCase("bearish")).count();
        long neutral = marketMoodList.size() - (bullish + bearish);

        if (bullish > bearish && bullish > neutral) return "Bullish";
        if (bearish > bullish && bearish > neutral) return "Bearish";
        return "Neutral"; 
    }

    private String generateDetailedAdvice(String riskProfile, String marketStatus) {
        StringBuilder advice = new StringBuilder();
        advice.append("<h2>📊 Investment Strategy Based on Your Risk Profile & Market Conditions</h2>");

        switch (riskProfile.toLowerCase()) {
            case "high":
                advice.append("<h3>🔥 High-Risk Investor</h3>")
                      .append(marketStatus.equals("Bullish") 
                          ? "<p>Markets are growing rapidly. Focus on <b>high-growth stocks, ETFs, and crypto investments</b>.</p>"
                          : "<p>⚠️ High volatility detected. Consider <b>reducing high-risk assets</b> and keeping some liquidity.</p>")
                      .append("<h4>💡 Recommended Investments:</h4>")
                      .append("<ul><li>Small Cap Stocks</li><li>Crypto</li><li>Growth ETFs</li></ul>");
                break;

            case "medium":
                advice.append("<h3>📈 Medium-Risk Investor</h3>")
                      .append(marketStatus.equals("Bullish") 
                          ? "<p>Markets are stable. Maintain a <b>balanced portfolio</b> of stocks and bonds.</p>"
                          : "<p>📉 Market downturn detected. Increase <b>bond exposure</b> and shift to defensive stocks.</p>")
                      .append("<h4>💡 Recommended Investments:</h4>")
                      .append("<ul><li>Large Cap Stocks</li><li>ETFs</li><li>60% Stocks, 30% Bonds, 10% Alternatives</li></ul>");
                break;

            case "low":
                advice.append("<h3>🔵 Low-Risk Investor</h3>")
                      .append(marketStatus.equals("Bullish") 
                          ? "<p>Market is positive, but focus on <b>stable, low-risk assets</b>.</p>"
                          : "<p>🛑 Market uncertainty detected. Stick to <b>bonds, gold, and cash equivalents</b>.</p>")
                      .append("<h4>💡 Recommended Investments:</h4>")
                      .append("<ul><li>Bonds</li><li>Fixed Deposits</li><li>Blue-Chip Stocks</li><li>Index Funds</li></ul>");
                break;

            default:
                advice.append("<h3>🚀 General Advice</h3>")
                      .append("<p>Diversify investments to <b>minimize risk and optimize returns</b>.</p>");
        }

        return advice.toString();
    }
}
