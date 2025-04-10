import os
import subprocess
import pandas as pd
from flask import Flask, jsonify, request, send_from_directory
from flask_cors import CORS
import yfinance as yf
from pypfopt.efficient_frontier import EfficientFrontier
from pypfopt.expected_returns import mean_historical_return
from pypfopt.risk_models import sample_cov

app = Flask(__name__)
CORS(app)

@app.route('/predict-lstm-batch', methods=['GET'])
def run_lstm_batch():
    try:
        result = subprocess.run(['python', 'training/lstm_batch_predictor.py'], capture_output=True, text=True)
        if result.returncode == 0:
            return jsonify({
                "message": "Batch LSTM prediction completed successfully.",
                "output": result.stdout
            })
        else:
            return jsonify({
                "error": "Prediction failed.",
                "details": result.stderr
            }), 500
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/optimize-portfolio', methods=['POST'])
def optimize_portfolio():
    default_tickers = ['AAPL', 'GOOGL', 'MS', 'JNJ', 'HD', 'COP', 'FDX']
    tickers = request.json.get('tickers') or default_tickers

    try:
        raw_data = yf.download(tickers, start="2022-01-01", end="2024-01-01", group_by='ticker', auto_adjust=True)

        if isinstance(raw_data.columns, pd.MultiIndex):
            data = raw_data.loc[:, (slice(None), 'Close')]
            data.columns = data.columns.droplevel(1)
        else:
            data = raw_data

        data.dropna(inplace=True)
        if data.empty:
            return jsonify({"error": "No valid historical data for selected tickers."}), 400

        mu = mean_historical_return(data)
        S = sample_cov(data)
        ef = EfficientFrontier(mu, S)
        weights = ef.max_sharpe()
        cleaned_weights = ef.clean_weights()
        cleaned_weights = {k: v for k, v in cleaned_weights.items() if k in tickers and v > 0}
        performance = ef.portfolio_performance(verbose=False)

        return jsonify({
            "tickers_used": tickers,
            "recommended_allocations": cleaned_weights,
            "expected_return": round(performance[0], 4),
            "volatility": round(performance[1], 4),
            "sharpe_ratio": round(performance[2], 4)
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500



@app.route('/plot/<filename>', methods=['GET'])
def serve_plot(filename):
    return send_from_directory('static', filename)

@app.route('/prediction-metrics', methods=['GET'])
def get_prediction_metrics():
    try:
        metrics_path = os.path.join('static', '..', 'stock_plots', 'stock_prediction_errors.csv')
        df = pd.read_csv(metrics_path)
        return jsonify(df.to_dict(orient='records'))
    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    app.run(port=5002)