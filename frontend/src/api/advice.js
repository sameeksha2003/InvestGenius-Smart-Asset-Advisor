import axios from "axios";

const API_BASE_URL = "http://localhost:5000/api";

export const fetchInvestmentAdvice = async (userId) => {
  try {
    const token = localStorage.getItem('token'); // Retrieve the JWT token from localStorage

    const response = await axios.get(`${API_BASE_URL}/advice/${userId}`, {
      headers: {
        Authorization: `Bearer ${token}`, // Attach token to the Authorization header
      },
    });

    return response.data;
  } catch (error) {
    console.error("Error fetching investment advice:", error);
    return "⚠️ Unable to fetch investment advice.";
  }
};
