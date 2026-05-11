import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { getApiErrorMessage } from '../services/apiError';
import { useToast } from '../contexts/ToastContext';
import { UserPlus } from 'lucide-react';
import '../styles/Login.css';

const Register: React.FC = () => {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { showToast } = useToast();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);

    try {
      await api.post('/gamevault/auth/registrar', { nome, email, senha });
      showToast({ title: 'Usuario cadastrado com sucesso.', variant: 'success' });
      navigate('/login');
    } catch (error) {
      showToast({
        title: 'Nao foi possivel concluir o cadastro.',
        message: getApiErrorMessage(error, 'Verifique os dados informados e tente novamente.'),
        variant: 'error',
      });
    } finally {
      setSubmitting(false);
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
            <label htmlFor="register-nome">Nome Completo</label>
            <input
              id="register-nome"
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="register-email">E-mail</label>
            <input
              id="register-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="register-senha">Senha</label>
            <input
              id="register-senha"
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn-primary" style={{ backgroundColor: 'var(--secondary)' }} disabled={submitting}>
            {submitting ? 'Cadastrando...' : 'Cadastrar'}
          </button>
        </form>

        <div className="auth-footer" style={{ marginTop: '1.5rem', textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Ja tem uma conta? <Link to="/login" style={{ color: 'var(--primary)', fontWeight: '600' }}>Faca login</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
