import React, { useState, useEffect } from "react";
import axios from "axios";

const MarketSentiment = () => {
  const [marketMood, setMarketMood] = useState("📊 Neutral");
  const [news, setNews] = useState([]);

  useEffect(() => {
    axios.get("http://localhost:5000/api/market-sentiment/analyze")
      .then(response => {
        setMarketMood(response.data.marketMood);
        setNews(response.data.news);
      })
      .catch(error => console.error("Error fetching market sentiment:", error));
  }, []);

  const getSentimentDetails = (sentiment) => {
    if (sentiment === "positive") return { color: "bg-green-100", icon: "✅", text: "text-green-600" };
    if (sentiment === "negative") return { color: "bg-red-100", icon: "❌", text: "text-red-600" };
    return { color: "bg-gray-100", icon: "⚠️", text: "text-gray-600" };
  };

  return (
    <div className="p-6 border rounded-lg shadow-lg bg-white">
      {}
      <h2 className="text-2xl font-bold flex items-center">
        📈 Market Sentiment: <span className="ml-2 text-blue-600">{marketMood}</span>
      </h2>

      {}
      <div className="mt-3 w-full h-2 bg-gray-300 rounded-lg">
        <div
          className={`h-2 rounded-lg transition-all duration-500 ${
            marketMood.includes("Positive") ? "bg-green-500 w-3/4" :
            marketMood.includes("Negative") ? "bg-red-500 w-1/4" : "bg-yellow-500 w-1/2"
          }`}
        />
      </div>

      {}
      <h3 className="mt-5 text-xl font-semibold">📰 Latest Financial News</h3>
      <div className="mt-3 grid grid-cols-1 gap-4">
        {news.map((article, index) => {
          const { color, icon, text } = getSentimentDetails(article.sentiment);
          return (
            <div key={index} className={`p-4 rounded-lg shadow-md ${color}`}>
              <h4 className={`text-lg font-semibold ${text}`}>{icon} {article.headline}</h4>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default MarketSentiment;
