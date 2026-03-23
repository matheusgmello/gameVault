import React from 'react';
import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import './styles/global.css';

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { signed, loading } = useAuth();

  if (loading) return <div>Carregando...</div>;
  if (!signed) return <Navigate to="/login" />;

  return <>{children}</>;
};

const router = createBrowserRouter([
  {
    path: "/login",
    element: <Login />,
  },
  {
    path: "/registrar",
    element: <Register />,
  },
  {
    path: "/",
    element: (
      <PrivateRoute>
        <Dashboard />
      </PrivateRoute>
    ),
  },
]);

function App() {
  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  );
}

export default App;
