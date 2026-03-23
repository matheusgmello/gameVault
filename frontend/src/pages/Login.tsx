import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { LogIn } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/Login.css';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const { signIn } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await signIn(email, senha);
      navigate('/');
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

        <div className="auth-footer" style={{ marginTop: '1.5rem', textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Não tem uma conta? <Link to="/registrar" style={{ color: 'var(--primary)', fontWeight: '600' }}>Cadastre-se</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
