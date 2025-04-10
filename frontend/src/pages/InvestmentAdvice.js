import { useState, useEffect, useContext } from "react";
import { fetchInvestmentAdvice } from "../api/advice";
import AuthContext from "../context/AuthContext";
import {
  Container,
  Typography,
  Paper,
  CircularProgress,
  Button,
  Box,
} from "@mui/material";
import axios from "axios";

const InvestmentAdvice = () => {
  const { user, token } = useContext(AuthContext);
  const [advice, setAdvice] = useState("");
  const [loading, setLoading] = useState(true);

  const [lstmOutput, setLstmOutput] = useState("");
  const [lstmLoading, setLstmLoading] = useState(false);

  useEffect(() => {
    const getAdvice = async () => {
      if (!user || !token) {
        setAdvice("User not authenticated. Please log in.");
        setLoading(false);
        return;
      }

      try {
        const data = await fetchInvestmentAdvice(user.id, token);
        setAdvice(data.advice || "No advice available.");
      } catch (error) {
        console.error("Error fetching investment advice:", error);
        setAdvice("Unable to fetch investment advice.");
      } finally {
        setLoading(false);
      }
    };

    getAdvice();
  }, [user, token]);

  const handleRunLSTM = async () => {
    setLstmLoading(true);
    try {
      const response = await axios.get("http://localhost:5002/predict-lstm-batch");
      setLstmOutput(response.data.output || "✅ Prediction completed.");
    } catch (error) {
      console.error("❌ Error running LSTM prediction:", error);
      setLstmOutput("❌ Failed to run prediction.");
    } finally {
      setLstmLoading(false);
    }
  };

  return (
    <Container style={{ padding: "20px" }}>
      <Typography variant="h4" gutterBottom>
        📊 Your Investment Advice
      </Typography>

      {loading ? (
        <CircularProgress />
      ) : (
        <Paper elevation={3} style={{ padding: "20px", backgroundColor: "#f3f4f6", marginBottom: "20px" }}>
          <Typography variant="h6">{advice}</Typography>
        </Paper>
      )}

      {/* LSTM Batch Prediction Section */}
      <Box mt={4}>
        <Typography variant="h5" gutterBottom>
          🤖 Stock Trend Forecast (LSTM)
        </Typography>

        <Button variant="contained" color="primary" onClick={handleRunLSTM} disabled={lstmLoading}>
          {lstmLoading ? "Running..." : "Run LSTM Prediction"}
        </Button>

        {lstmOutput && (
          <Paper elevation={2} style={{ marginTop: "20px", padding: "15px", background: "#eef2f7" }}>
            <Typography variant="subtitle1" gutterBottom>
              📈 Prediction Output:
            </Typography>
            <Typography variant="body2" style={{ whiteSpace: "pre-wrap" }}>
              {lstmOutput}
            </Typography>
          </Paper>
        )}
      </Box>
    </Container>
  );
};

export default InvestmentAdvice;
