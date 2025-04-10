import axios from "axios";

export const getInvestmentAdvice = async () => {
  const token = localStorage.getItem("token");  

  if (!token) {
    console.error("No token found, user must log in.");
    return;
  }

  try {
    const response = await axios.get("http://localhost:5000/api/investment/advice", {
      headers: {
        Authorization: `Bearer ${token}`  
      }
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching investment advice:", error);
    throw error;
  }
};
