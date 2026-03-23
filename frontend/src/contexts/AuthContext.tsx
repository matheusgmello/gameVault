import React, { createContext, useState, useContext, useEffect } from 'react';
import api from '../services/api';

interface User {
  id: number;
  nome: string;
  email: string;
}

interface AuthContextData {
  signed: boolean;
  user: User | null;
  loading: boolean;
  signIn(email: string, senha: string): Promise<void>;
  signOut(): void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storagedUser = localStorage.getItem('gamevault_user');
    const storagedToken = localStorage.getItem('gamevault_token');

    if (storagedUser && storagedToken) {
      setUser(JSON.parse(storagedUser));
    }
    setLoading(false);
  }, []);

  async function signIn(email: string, senha: string) {
    const response = await api.post('/gamevault/auth/login', { email, senha });

    const { token, usuario } = response.data;

    setUser(usuario);
    localStorage.setItem('gamevault_user', JSON.stringify(usuario));
    localStorage.setItem('gamevault_token', token);
  }

  function signOut() {
    localStorage.removeItem('gamevault_user');
    localStorage.removeItem('gamevault_token');
    setUser(null);
  }

  return (
    <AuthContext.Provider value={{ signed: !!user, user, loading, signIn, signOut }}>
      {children}
    </AuthContext.Provider>
  );
};

export function useAuth() {
  const context = useContext(AuthContext);
  return context;
}
