import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { LogIn } from 'lucide-react';
import '../styles/Login.css';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const { signIn } = useAuth();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await signIn(email, senha);
    } catch (error) {
      alert('Falha no login, verifique suas credenciais.');
    }
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="logo-icon">
            <LogIn size={32} />
          </div>
          <h1>GameVault</h1>
          <p>Seu acervo de games em um só lugar</p>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>E-mail</label>
            <input 
              type="email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input 
              type="password" 
              value={senha} 
              onChange={e => setSenha(e.target.value)} 
              required 
            />
          </div>
          <button type="submit" className="btn-primary">Entrar</button>
        </form>
      </div>
    </div>
  );
};

export default Login;
