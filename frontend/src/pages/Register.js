import { useState } from "react";
import { registerUser } from "../api/auth";
import { useNavigate } from "react-router-dom";
import "../styles/Form.css"; 

const Register = () => {
    const [form, setForm] = useState({ name: "", email: "", password: "", age: "", occupation: "", annualIncome: "", riskCategory: "", investmentGoals: "" });
    const navigate = useNavigate();

    const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await registerUser(form);
            alert("Registration Successful!");
            navigate("/login");
        } catch (error) {
            alert("Error registering user");
        }
    };

    return (
        <div className="container">
            <h2>Register</h2>
            <form onSubmit={handleSubmit}>
                <input type="text" name="name" placeholder="Name" onChange={handleChange} required />
                <input type="email" name="email" placeholder="Email" onChange={handleChange} required />
                <input type="password" name="password" placeholder="Password" onChange={handleChange} required />
                <input type="number" name="age" placeholder="Age" onChange={handleChange} required />
                <input type="text" name="occupation" placeholder="Occupation" onChange={handleChange} required />
                <input type="number" name="annualIncome" placeholder="Annual Income" onChange={handleChange} required />
                <input type="text" name="riskCategory" placeholder="Risk Category" onChange={handleChange} required />
                <input type="text" name="investmentGoals" placeholder="Investment Goals" onChange={handleChange} required />
                <button type="submit">Register</button>
            </form>
        </div>
    );
};

export default Register;
