import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import UserList from './components/UserList';
import { useAuth } from './context/AuthContext';

const PrivateRoute: React.FC<{ children: React.ReactElement }> = ({ children }) => {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? children : <Navigate to="/login" />;
};

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route
                    path="/users"
                    element={
                        <PrivateRoute>
                            <UserList />
                        </PrivateRoute>
                    }
                />
                <Route path="/" element={<Navigate to="/users" />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
