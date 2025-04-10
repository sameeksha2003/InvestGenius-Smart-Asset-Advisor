import { Link } from "react-router-dom";
import { useContext } from "react";
import { AuthContext } from "../context/AuthContext"; 

const Navbar = () => {
    const { token, logout } = useContext(AuthContext);

    return (
        <nav>
            <Link to="/">Home</Link>
            {token ? (
                <>
                    <Link to="/dashboard">Dashboard</Link>
                    <button onClick={logout}>Logout</button>
                </>
            ) : (
                <>
                    <Link to="/login">Login</Link>
                    <Link to="/register">Register</Link>
                </>
            )}
        </nav>
    );
};

export default Navbar;
