import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { getApiErrorMessage } from '../services/apiError';
import { LogIn } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';
import '../styles/Login.css';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { signIn } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);

    try {
      await signIn(email, senha);
      showToast({ title: 'Login realizado com sucesso.', variant: 'success' });
      navigate('/');
    } catch (error) {
      showToast({
        title: 'Nao foi possivel entrar.',
        message: getApiErrorMessage(error, 'Verifique suas credenciais e tente novamente.'),
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
          <div className="logo-icon">
            <LogIn size={32} />
          </div>
          <h1>GameVault</h1>
          <p>Seu acervo de games em um so lugar</p>
        </div>

        <div className="demo-access">
          <div>
            <strong>Acesso demo</strong>
            <p>Preencha automaticamente a conta de demonstracao para testar a interface.</p>
          </div>
          <button
            type="button"
            className="demo-fill-button"
            onClick={() => {
              setEmail('matheus@email.com');
              setSenha('12345');
            }}
          >
            Usar conta demo
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="login-email">E-mail</label>
            <input
              id="login-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="login-senha">Senha</label>
            <input
              id="login-senha"
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="auth-footer" style={{ marginTop: '1.5rem', textAlign: 'center' }}>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Nao tem uma conta? <Link to="/registrar" style={{ color: 'var(--primary)', fontWeight: '600' }}>Cadastre-se</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Login;
