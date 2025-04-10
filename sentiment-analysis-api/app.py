from flask import Flask, request, jsonify
import torch
from transformers import BertTokenizer, BertForSequenceClassification

app = Flask(__name__)

tokenizer = BertTokenizer.from_pretrained('yiyanghkust/finbert-tone')
model = BertForSequenceClassification.from_pretrained('yiyanghkust/finbert-tone')

def analyze_sentiment(text):
    inputs = tokenizer(text, return_tensors="pt", truncation=True, padding=True, max_length=512)
    with torch.no_grad():
        logits = model(**inputs).logits
    sentiment = torch.argmax(logits, dim=1).item()
    
    sentiment_map = {0: 'negative', 1: 'neutral', 2: 'positive'}
    return sentiment_map[sentiment]
 
@app.route('/api/sentiment', methods=['POST'])
def get_sentiment():
    try:
        data = request.get_json()
        if 'text' not in data:
            return jsonify({'error': 'Missing "text" field in the request'}), 400
        
        text = data['text']
        sentiment = analyze_sentiment(text)
        
        return jsonify({'sentiment': sentiment}), 200
    except Exception as e:
        return jsonify({'error': f"An error occurred: {str(e)}"}), 400

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)  
