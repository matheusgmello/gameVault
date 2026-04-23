import React, { createContext, useContext, useState } from 'react';
import api from '../services/api';
import { jwtDecode } from 'jwt-decode';

interface User {
  id: number;
  nome: string;
  email: string;
}

interface JWTPayload {
  usuarioId: number;
  nome: string;
  sub: string;
  exp: number;
}

interface AuthContextData {
  signed: boolean;
  user: User | null;
  loading: boolean;
  signIn(email: string, senha: string): Promise<void>;
  signOut(): void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

function getStoredUser(): User | null {
  const storagedUser = localStorage.getItem('gamevault_user');
  const storagedToken = localStorage.getItem('gamevault_token');

  if (!storagedUser || !storagedToken) {
    return null;
  }

  try {
    return JSON.parse(storagedUser) as User;
  } catch {
    localStorage.removeItem('gamevault_user');
    localStorage.removeItem('gamevault_token');
    return null;
  }
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => getStoredUser());
  const [loading] = useState(false);

  async function signIn(email: string, senha: string) {
    const response = await api.post('/gamevault/auth/login', { email, senha });
    const { token } = response.data;

    const decoded = jwtDecode<JWTPayload>(token);

    const usuario: User = {
      id: decoded.usuarioId,
      nome: decoded.nome,
      email: decoded.sub,
    };

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

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  return useContext(AuthContext);
}
