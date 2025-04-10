import { createContext, useState, useEffect } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        console.log("🔄 Loading stored user from localStorage:", storedUser);

        if (storedUser) {
            try {
                const parsedUser = JSON.parse(storedUser);
                console.log("✅ Parsed user:", parsedUser);
                setUser(parsedUser); 
            } catch (error) {
                console.error("❌ Error parsing user data:", error);
                localStorage.removeItem("user");
            }
        }
    }, []);

    const login = (userData) => {
        console.log("🔥 Setting user in context:", userData);
        setUser(userData);
        localStorage.setItem("user", JSON.stringify(userData));
    };

    const logout = () => {
        console.log("🚪 Logging out...");
        localStorage.removeItem("user");
        localStorage.removeItem("token");
        setUser(null);
    };

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            try {
                setUser(JSON.parse(storedUser));
            } catch (error) {
                console.error("❌ Error parsing user data:", error);
                localStorage.removeItem("user");
            }
        }
    }, []);
    
    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};
