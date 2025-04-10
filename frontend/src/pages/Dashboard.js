import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";  
import axios from "axios";
import "../styles/dashboard.css";

const Dashboard = () => {
  const navigate = useNavigate(); 
  const [advice, setAdvice] = useState(""); 
  const [loading, setLoading] = useState(true);
  const [marketMood, setMarketMood] = useState("Neutral"); 
  const [marketMoodImage, setMarketMoodImage] = useState("/images/neutral-market.jpg");
  const [news, setNews] = useState([]); 
  const [portfolioResult, setPortfolioResult] = useState(null);
  const [optimizing, setOptimizing] = useState(false);
  const [predictionResult, setPredictionResult] = useState("");
  const [predictionLoading, setPredictionLoading] = useState(false);
  const [metrics, setMetrics] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      await fetchInvestmentAdvice();
      await fetchMarketSentiment();
      await fetchPredictionMetrics();
    };
    fetchData();
  }, []);

  const fetchInvestmentAdvice = async () => {
    try {
      const token = localStorage.getItem("token"); 
      const response = await axios.get("http://localhost:5000/api/investment/advice", {
        headers: { Authorization: `Bearer ${token}` },
      });

      console.log("✅ Investment Advice Response:", response.data);
      setAdvice(response.data);

      if (response.data.includes("Extreme Greed")) setMarketMood("Extreme Greed");
      else if (response.data.includes("Greed")) setMarketMood("Greed");
      else if (response.data.includes("Extreme Fear")) setMarketMood("Extreme Fear");
      else if (response.data.includes("Fear")) setMarketMood("Fear");
      else setMarketMood("Neutral");

    } catch (error) {
      console.error("❌ Error fetching investment advice:", error);
    } finally {
      setLoading(false);
    }
  };

  const fetchMarketSentiment = async () => {
    try {
      const response = await axios.get("http://localhost:5000/api/market-sentiment/analyze");
      console.log("✅ Market Sentiment Response:", response.data);
      setMarketMood(response.data.marketMood);  
      setNews(response.data.summary);  

      const moodImages = {
        "📈 Positive": "/images/positive-market.jpg",
        "📉 Negative": "/images/negative-market.jpg",
        "Neutral": "/images/neutral-market.jpg"
      };

      setMarketMoodImage(moodImages[response.data.marketMood] || "/images/neutral-market.jpg");

    } catch (error) {
      console.error("❌ Error fetching market sentiment:", error);
    }
  };

  const fetchPredictionMetrics = async () => {
    try {
      const res = await axios.get("http://localhost:5002/prediction-metrics");
      setMetrics(res.data);
    } catch (err) {
      console.error("❌ Error fetching prediction metrics:", err);
    }
  };

  const runLSTMForecast = async () => {
    setPredictionLoading(true);
    try {
      const response = await axios.get("http://localhost:5002/predict-lstm-batch");
      console.log("✅ LSTM Prediction Output:", response.data);
      const output = response.data.output || "Predictions completed.";
      const mseMatch = output.match(/MSE: [\d.]+/);
      const rmseMatch = output.match(/RMSE: [\d.]+/);
      const cleanedOutput = `${mseMatch ? mseMatch[0] : ""}\n${rmseMatch ? rmseMatch[0] : ""}`;
      setPredictionResult(cleanedOutput.trim());
    } catch (error) {
      console.error("❌ LSTM Prediction Error:", error);
      setPredictionResult("⚠️ Some tickers could not be predicted due to missing data.");    
    } finally {
      setPredictionLoading(false);
    }
  };

  const handlePortfolioOptimize = async () => {
    setOptimizing(true);
    try {
      const response = await axios.post("http://localhost:5002/optimize-portfolio", {
        tickers: ['AAPL', 'MSFT', 'GOOGL', 'AMZN', 'NVDA']
      });
      setPortfolioResult(response.data);
    } catch (err) {
      console.error("❌ Portfolio Optimization Error:", err);
    } finally {
      setOptimizing(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h1 className="dashboard-title">📊 Investment Dashboard</h1>
        <button className="logout-btn" onClick={handleLogout}>🚪 Logout</button>
      </div>

      {loading ? (
        <p className="loading-text">Loading investment advice...</p>
      ) : (
        <div className="investment-advice">
          <h2>💡 Investment Advice</h2>
          <div className="advice-text" dangerouslySetInnerHTML={{ __html: advice }} />

          <div className="market-mood">
            <h3>📈 Market Sentiment</h3>
            <p>Current Market Mood: <strong>{marketMood}</strong></p>
            <img 
              src={marketMoodImage} 
              alt="Market Mood" 
              className="market-mood-img"
              onError={(e) => e.currentTarget.src = "/images/default-market-mood.jpg"} 
            />
          </div>

          <div className="financial-news">
            <h3>📰 Market News Summary</h3>
            <ul className="news-list">
              {Array.isArray(news) ? (
                news.map((item, index) => (
                  <li key={index}>🔹 {item}</li>
                ))
              ) : (
                news?.split("• ").map((item, index) => (
                  item.trim() && <li key={index}>🔹 {item.trim()}</li>
                ))
              )}
            </ul>
          </div>
        </div>
      )}

      <div className="prediction-section">
        <h3>📊 Prediction Metrics</h3>
        {metrics.length > 0 ? (
          <table className="metric-table">
            <thead>
              <tr>
                <th>Ticker</th>
                <th>RMSE</th>
                <th>MAE</th>
              </tr>
            </thead>
            <tbody>
              {metrics.map((m) => (
                <tr key={m.Ticker}>
                  <td>{m.Ticker}</td>
                  <td>{m.RMSE}</td>
                  <td>{m.MAE}</td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>📉 No prediction metrics available yet.</p>
        )}
      </div>

      <div className="lstm-plot-section">
        <h3>📊 AAPL Forecast Plot</h3>
        <button onClick={runLSTMForecast} disabled={predictionLoading}>
          {predictionLoading ? "Running Forecast..." : "Run LSTM Forecast"}
        </button>

        {predictionResult && (
          <pre className="prediction-result">
            📈 {predictionResult}
          </pre>
        )}

        <img
          src={`http://localhost:5002/plot/lstm_plot.png?ts=${Date.now()}`}
          alt="AAPL LSTM Forecast"
          className="stock-plot"
          style={{ maxWidth: "100%", border: "1px solid #ccc", borderRadius: "8px", marginTop: "10px" }}
        />
      </div>

      <div className="portfolio-section">
        <h3>📈 Portfolio Optimization</h3>
        <button onClick={handlePortfolioOptimize} disabled={optimizing}>
          {optimizing ? "Optimizing..." : "Optimize Portfolio"}
        </button>

        {portfolioResult && (
          <div className="portfolio-results">
            <h4>📊 Recommended Allocations:</h4>
            <ul>
              {Object.entries(portfolioResult.recommended_allocations)
                .filter(([_, weight]) => weight > 0)
                .map(([ticker, weight]) => (
                  <li key={ticker}>📌 <strong>{ticker}</strong>: {(weight * 100).toFixed(2)}%</li>
                ))}
            </ul>
            <p>📈 <strong>Expected Return:</strong> {(portfolioResult.expected_return * 100).toFixed(2)}%</p>
            <p>📉 <strong>Volatility:</strong> {(portfolioResult.volatility * 100).toFixed(2)}%</p>
            <p>📊 <strong>Sharpe Ratio:</strong> {portfolioResult.sharpe_ratio.toFixed(4)}</p>
            <p style={{ marginTop: "8px", fontWeight: "bold" }}>
              💰 <em>Suggested Focus:</em> Invest more in <strong>{
                Object.entries(portfolioResult.recommended_allocations)
                  .reduce((max, curr) => curr[1] > max[1] ? curr : max, ["", 0])[0]
              }</strong>.
            </p>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
