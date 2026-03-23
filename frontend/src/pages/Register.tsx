import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { UserPlus } from 'lucide-react';
import '../styles/Login.css'; // Reutilizando estilos base

const Register: React.FC = () => {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await api.post('/gamevault/auth/registrar', { nome, email, senha });
      alert('Usuário cadastrado com sucesso!');
      navigate('/login');
    } catch (error) {
      alert('Falha no cadastro. Verifique os dados ou se o e-mail já existe.');
    }
  }

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <div className="logo-icon" style={{ backgroundColor: 'var(--secondary)' }}>
            <UserPlus size={32} />
          </div>
          <h1>Criar Conta</h1>
          <p>Junte-se ao GameVault hoje</p>
        </div>
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome Completo</label>
            <input 
              type="text" 
              value={nome} 
              onChange={e => setNome(e.target.value)} 
              required 
            />
          </div>
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
          <button type="submit" className="btn-primary" style={{ backgroundColor: 'var(--secondary)' }}>
            Cadastrar
          </button>
        </form>

        <div className="auth-footer" style={{ marginTop: '1.5rem', textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Já tem uma conta? <Link to="/login" style={{ color: 'var(--primary)', fontWeight: '600' }}>Faça Login</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
