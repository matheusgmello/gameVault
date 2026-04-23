import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import { Clock, Heart, Layout, LogOut, Gamepad2, Layers, Monitor, Star, Trophy } from 'lucide-react';
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

const Dashboard: React.FC = () => {
  const { user, signOut } = useAuth();
  const [activeTab, setActiveTab] = useState<'stats' | 'games' | 'genres' | 'platforms'>('stats');
  const [stats, setStats] = useState({
    totalJogos: 0,
    totalGeneros: 0,
    totalPlataformas: 0,
    mediaNota: 0,
    totalFavoritos: 0,
    totalHoras: 0,
  });
  const [platformChartData, setPlatformChartData] = useState<ChartItem[]>([]);
  const [statusChartData, setStatusChartData] = useState<ChartItem[]>([]);
  const [topGamesData, setTopGamesData] = useState<ChartItem[]>([]);

  useEffect(() => {
    let mounted = true;

    api.get<GameStatsResponse>('/gamevault/jogo/estatisticas')
      .then((response) => {
        if (!mounted) {
          return;
        }

        const data = response.data;
        setStats({
          totalJogos: data.totalJogos,
          totalGeneros: data.totalGeneros,
          totalPlataformas: data.totalPlataformas,
          mediaNota: data.mediaNota,
          totalFavoritos: data.totalFavoritos,
          totalHoras: data.totalHoras,
        });
        setPlatformChartData(data.jogosPorPlataforma);
        setStatusChartData(data.jogosPorStatus.map((item) => ({
          ...item,
          name: STATUS_LABELS[item.name] ?? item.name,
        })));
        setTopGamesData(data.topJogos);
      })
      .catch((error) => {
        console.error('Erro ao carregar estatisticas', error);
      });

    return () => {
      mounted = false;
    };
  }, []);

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
          <button onClick={signOut} className="btn-logout">
            <LogOut size={20} /> Sair
          </button>
        </div>
      </aside>

      <main className="content">
        {activeTab === 'stats' && (
          <>
            <header className="content-header">
              <h1>Ola, {user?.nome}</h1>
              <p>Aqui esta o resumo da sua colecao.</p>
            </header>

            <div className="stats-grid">
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
            </div>

            <div className="charts-grid">
              <div className="chart-container">
                <h3>Jogos por Plataforma</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={platformChartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={80}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {platformChartData.map((_, index) => (
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
                  <BarChart data={statusChartData}>
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
                  <BarChart data={topGamesData} layout="vertical" margin={{ left: 40 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#dedbd0" />
                    <XAxis type="number" stroke="#68736f" domain={[0, 10]} />
                    <YAxis type="category" dataKey="name" stroke="#68736f" width={130} />
                    <Tooltip />
                    <Bar dataKey="value" fill="#ff7a59" radius={[0, 4, 4, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
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
