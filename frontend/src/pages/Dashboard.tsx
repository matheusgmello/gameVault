import React, { useEffect, useMemo, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { getApiErrorMessage } from '../services/apiError';
import api from '../services/api';
import { Clock, Heart, Layout, LogOut, Gamepad2, Layers, Monitor, Star, Trophy, Sparkles, Plus } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import '../styles/Dashboard.css';

import Catalog from './Catalog';
import SimpleManager from './SimpleManager';

const COLORS = ['#0f766e', '#ff7a59', '#d9466b', '#2f6f95', '#c59b2c'];

interface ChartItem {
  name: string;
  value: number;
}

interface GameStatsResponse {
  totalJogos: number;
  totalGeneros: number;
  totalPlataformas: number;
  mediaNota: number;
  totalFavoritos: number;
  totalHoras: number;
  jogosPorPlataforma: ChartItem[];
  jogosPorStatus: ChartItem[];
  topJogos: ChartItem[];
}

const STATUS_LABELS: Record<string, string> = {
  WISHLIST: 'Wishlist',
  JOGANDO: 'Jogando',
  ZERADO: 'Zerado',
  ABANDONADO: 'Abandonado',
};

const EMPTY_STATS: GameStatsResponse = {
  totalJogos: 0,
  totalGeneros: 0,
  totalPlataformas: 0,
  mediaNota: 0,
  totalFavoritos: 0,
  totalHoras: 0,
  jogosPorPlataforma: [],
  jogosPorStatus: [],
  topJogos: [],
};

const Dashboard: React.FC = () => {
  const { user, signOut } = useAuth();
  const { showToast } = useToast();
  const [activeTab, setActiveTab] = useState<'stats' | 'games' | 'genres' | 'platforms'>('stats');
  const [stats, setStats] = useState<GameStatsResponse>(EMPTY_STATS);
  const [loadingStats, setLoadingStats] = useState(true);

  useEffect(() => {
    let mounted = true;

    api.get<GameStatsResponse>('/gamevault/jogo/estatisticas')
      .then((response) => {
        if (!mounted) {
          return;
        }

        const data = response.data;
        setStats({
          ...data,
          jogosPorStatus: data.jogosPorStatus.map((item) => ({
            ...item,
            name: STATUS_LABELS[item.name] ?? item.name,
          })),
        });
      })
      .catch((error) => {
        if (!mounted) {
          return;
        }

        showToast({
          title: 'Nao foi possivel carregar o dashboard.',
          message: getApiErrorMessage(error, 'Atualize a pagina e tente novamente.'),
          variant: 'error',
        });
      })
      .finally(() => {
        if (mounted) {
          setLoadingStats(false);
        }
      });

    return () => {
      mounted = false;
    };
  }, [showToast]);

  const heroSummary = useMemo(() => {
    if (stats.totalJogos === 0) {
      return 'Sua biblioteca ainda esta vazia. Cadastre os primeiros jogos para liberar estatisticas e comparacoes.';
    }

    if (stats.totalFavoritos > 0) {
      return `Voce ja organizou ${stats.totalJogos} jogos, com ${stats.totalFavoritos} favoritos e ${stats.totalHoras} horas registradas.`;
    }

    return `Voce ja organizou ${stats.totalJogos} jogos e registrou ${stats.totalHoras} horas na sua biblioteca.`;
  }, [stats.totalFavoritos, stats.totalHoras, stats.totalJogos]);

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Gamepad2 color="#0f766e" size={32} />
          <span>GameVault</span>
        </div>
        <nav className="sidebar-nav">
          <button
            className={activeTab === 'stats' ? 'active' : ''}
            onClick={() => setActiveTab('stats')}
          >
            <Layout size={20} /> Dashboard
          </button>
          <button
            className={activeTab === 'games' ? 'active' : ''}
            onClick={() => setActiveTab('games')}
          >
            <Gamepad2 size={20} /> Meus Jogos
          </button>
          <button
            className={activeTab === 'genres' ? 'active' : ''}
            onClick={() => setActiveTab('genres')}
          >
            <Layers size={20} /> Generos
          </button>
          <button
            className={activeTab === 'platforms' ? 'active' : ''}
            onClick={() => setActiveTab('platforms')}
          >
            <Monitor size={20} /> Plataformas
          </button>
        </nav>
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <span className="sidebar-user-label">Conta ativa</span>
            <strong>{user?.nome}</strong>
            <span>{user?.email}</span>
          </div>
          <button onClick={signOut} className="btn-logout">
            <LogOut size={20} /> Sair
          </button>
        </div>
      </aside>

      <main className="content">
        {activeTab === 'stats' && (
          <>
            <header className="content-header">
              <div>
                <h1>Ola, {user?.nome}</h1>
                <p>{heroSummary}</p>
              </div>
              <div className="content-badge">
                <Sparkles size={16} /> Biblioteca pessoal
              </div>
            </header>

            <div className="stats-grid">
              {loadingStats ? (
                Array.from({ length: 6 }).map((_, index) => (
                  <div key={index} className="stat-card stat-card-skeleton">
                    <div className="stat-icon-skeleton" />
                    <div className="stat-copy-skeleton">
                      <div className="skeleton-line short" />
                      <div className="skeleton-line medium" />
                    </div>
                  </div>
                ))
              ) : (
                <>
                  <div className="stat-card">
                    <Gamepad2 className="icon" />
                    <div>
                      <h3>{stats.totalJogos}</h3>
                      <p>Jogos</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <Layers className="icon" />
                    <div>
                      <h3>{stats.totalGeneros}</h3>
                      <p>Generos</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <Monitor className="icon" />
                    <div>
                      <h3>{stats.totalPlataformas}</h3>
                      <p>Plataformas</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <Star className="icon" />
                    <div>
                      <h3>{stats.mediaNota.toFixed(1)}</h3>
                      <p>Media de Notas</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <Heart className="icon" />
                    <div>
                      <h3>{stats.totalFavoritos}</h3>
                      <p>Favoritos</p>
                    </div>
                  </div>
                  <div className="stat-card">
                    <Clock className="icon" />
                    <div>
                      <h3>{stats.totalHoras}h</h3>
                      <p>Horas Jogadas</p>
                    </div>
                  </div>
                </>
              )}
            </div>

            {!loadingStats && stats.totalJogos === 0 ? (
              <section className="dashboard-empty">
                <Gamepad2 size={28} />
                <h3>Seu dashboard ganha vida quando a biblioteca comeca a crescer</h3>
                <p>Cadastre jogos com nota, status e plataforma para acompanhar seus favoritos, horas jogadas e distribuicao da colecao.</p>
                <button className="btn-add" onClick={() => setActiveTab('games')}>
                  <Plus size={18} /> Adicionar primeiro jogo
                </button>
              </section>
            ) : (
              <div className="charts-grid">
                <div className="chart-container">
                  <h3>Jogos por Plataforma</h3>
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={stats.jogosPorPlataforma}
                        cx="50%"
                        cy="50%"
                        innerRadius={60}
                        outerRadius={80}
                        paddingAngle={5}
                        dataKey="value"
                      >
                        {stats.jogosPorPlataforma.map((_, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip />
                    </PieChart>
                  </ResponsiveContainer>
                </div>

                <div className="chart-container">
                  <h3>Jogos por Status</h3>
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={stats.jogosPorStatus}>
                      <CartesianGrid strokeDasharray="3 3" stroke="#dedbd0" />
                      <XAxis dataKey="name" stroke="#68736f" />
                      <YAxis stroke="#68736f" allowDecimals={false} />
                      <Tooltip />
                      <Bar dataKey="value" fill="#0f766e" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>

                <div className="chart-container chart-container-wide">
                  <div className="chart-title-row">
                    <Trophy size={20} />
                    <h3>Top jogos por nota</h3>
                  </div>
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={stats.topJogos} layout="vertical" margin={{ left: 40 }}>
                      <CartesianGrid strokeDasharray="3 3" stroke="#dedbd0" />
                      <XAxis type="number" stroke="#68736f" domain={[0, 10]} />
                      <YAxis type="category" dataKey="name" stroke="#68736f" width={130} />
                      <Tooltip />
                      <Bar dataKey="value" fill="#ff7a59" radius={[0, 4, 4, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            )}
          </>
        )}

        {activeTab === 'games' && <Catalog />}
        {activeTab === 'genres' && <SimpleManager type="genero" title="Generos" />}
        {activeTab === 'platforms' && <SimpleManager type="plataforma" title="Plataformas" />}
      </main>
    </div>
  );
};

export default Dashboard;
