import React, { createContext, useContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';

interface User {
    sub: string;
    roles: string[];
    // Add other claims
}

interface AuthContextType {
    isAuthenticated: boolean;
    user: User | null;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [user, setUser] = useState<User | null>(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode<any>(token);
                // Check expiration
                if (decoded.exp * 1000 > Date.now()) {
                    setIsAuthenticated(true);
                    setUser({
                        sub: decoded.sub,
                        roles: [] // Extract roles if available in token claims
                    });
                } else {
                    localStorage.removeItem('token');
                }
            } catch (e) {
                localStorage.removeItem('token');
            }
        }
    }, []);

    const login = (token: string) => {
        localStorage.setItem('token', token);
        const decoded = jwtDecode<any>(token);
        setIsAuthenticated(true);
        setUser({
            sub: decoded.sub,
            roles: []
        });
    };

    const logout = () => {
        localStorage.removeItem('token');
        setIsAuthenticated(false);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
