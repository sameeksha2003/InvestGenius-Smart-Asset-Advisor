import os
import sys
sys.stdout.reconfigure(line_buffering=True)
import yfinance as yf
import pandas as pd
import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from sklearn.preprocessing import MinMaxScaler
from sklearn.metrics import mean_squared_error, mean_absolute_error
from ta.momentum import RSIIndicator
from keras.models import Sequential
from keras.layers import LSTM, Dense, Dropout, Input
import math
from concurrent.futures import ThreadPoolExecutor, as_completed
import tensorflow.keras.backend as K

tickers = [
    'AAPL', 'GOOGL', 'MS', 'JNJ', 'HD', 'COP', 'FDX'
]
sequence_length = 60
epochs = 5  

# Paths
base_dir = os.path.dirname(os.path.abspath(__file__))
static_path = os.path.join(base_dir, '..', 'static')
plots_path = os.path.join(base_dir, '..', 'stock_plots')
os.makedirs(static_path, exist_ok=True)
os.makedirs(plots_path, exist_ok=True)

# Load or fetch data
csv_path = os.path.join(base_dir, "cached_data.csv")
if os.path.exists(csv_path):
    data = pd.read_csv(csv_path, header=[0, 1], index_col=0)
else:
    data = yf.download(tickers, start="2020-01-01", end="2024-01-01", group_by="ticker")
    data.to_csv(csv_path)

# ----------------------------------------
error_df = pd.DataFrame(columns=["Ticker", "RMSE", "MAE"])

def process_stock(stock):
    print(f"Processing {stock}...")
    try:
        if stock not in data.columns.levels[0]:
            return stock, None, None, "Missing stock data"

        df = data[stock].copy()
        if df is None or df.empty or len(df) < sequence_length + 1:
            return stock, None, None, "Insufficient data"

        df.dropna(inplace=True)

        if 'Close' not in df.columns:
            return stock, None, None, "Missing 'Close' column"

        # RSI
        try:
            df['RSI'] = RSIIndicator(close=df['Close']).rsi()
        except Exception as e:
            return stock, None, None, f"RSI calc error: {e}"

        df.dropna(inplace=True)
        if len(df) < sequence_length + 1:
            return stock, None, None, "Not enough data after RSI"

        df_model = df[['Close', 'RSI']]
        if df_model.isnull().any().any():
            return stock, None, None, "NaNs in features"

        # Scale
        scaler = MinMaxScaler()
        try:
            scaled_data = scaler.fit_transform(df_model)
        except Exception as e:
            return stock, None, None, f"Scaler error: {e}"

        # Sequence
        X, y = [], []
        for i in range(sequence_length, len(scaled_data)):
            X.append(scaled_data[i-sequence_length:i])
            y.append(scaled_data[i][0])

        if len(X) < 10:
            return stock, None, None, "Too few sequences"

        X, y = np.array(X), np.array(y)
        split = int(0.8 * len(X))
        X_train, X_test = X[:split], X[split:]
        y_train, y_test = y[:split], y[split:]

        if len(X_test) == 0 or len(y_test) == 0:
            return stock, None, None, "Empty test set"

        # Model
        model = Sequential([
            Input(shape=(X.shape[1], X.shape[2])),
            LSTM(64, return_sequences=True),
            Dropout(0.2),
            LSTM(64),
            Dense(1)
        ])
        model.compile(optimizer='adam', loss='mean_squared_error')
        model.fit(X_train, y_train, epochs=epochs, batch_size=32, verbose=0)

        predicted = model.predict(X_test)

        # Handle inverse transform safely
        try:
            predicted_ext = np.hstack((predicted, np.zeros((len(predicted), 1))))
            true_ext = np.hstack((y_test.reshape(-1, 1), np.zeros((len(y_test), 1))))
            predicted_prices = scaler.inverse_transform(predicted_ext)[:, 0]
            true_prices = scaler.inverse_transform(true_ext)[:, 0]
        except Exception as e:
            return stock, None, None, f"Inverse transform error: {e}"

        rmse = math.sqrt(mean_squared_error(true_prices, predicted_prices))
        mae = mean_absolute_error(true_prices, predicted_prices)

        # Plot
        plt.figure(figsize=(12, 5))
        plt.plot(true_prices, label="Actual", color='blue')
        plt.plot(predicted_prices, label="Predicted", color='red')
        plt.title(f"{stock} Stock Prediction\n(RMSE: {rmse:.2f}, MAE: {mae:.2f})")
        plt.xlabel("Days")
        plt.ylabel("Price")
        plt.legend()
        plt.tight_layout()

        plot_file = f"{stock}_prediction.png"
        plt.savefig(os.path.join(plots_path, plot_file))
        if stock == 'AAPL':
            plt.savefig(os.path.join(static_path, 'lstm_plot.png'))
        plt.close()

        K.clear_session()
        return stock, rmse, mae, None

    except Exception as e:
        return stock, None, None, str(e)

# -------------------- MULTITHREAD --------------------
results = []
with ThreadPoolExecutor(max_workers=4) as executor:
    futures = [executor.submit(process_stock, ticker) for ticker in tickers]
    for future in as_completed(futures):
        stock, rmse, mae, error = future.result()
        if error:
                print(f" Failed for {stock}: {error}")
        else:
                error_df.loc[len(error_df)] = [stock, round(rmse, 2), round(mae, 2)]
                print(f"{stock} done. RMSE: {rmse:.2f}, MAE: {mae:.2f}")


# Save error metrics
error_csv_path = os.path.join(plots_path, "stock_prediction_errors.csv")
error_df.to_csv(error_csv_path, index=False)
print(f"\n Summary saved at: {error_csv_path}")