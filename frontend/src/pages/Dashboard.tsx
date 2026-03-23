import React, { useEffect, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import api from '../services/api';
import { Layout, LogOut, Gamepad2, Layers, Monitor, Star } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';
import '../styles/Dashboard.css';

import Catalog from './Catalog';
import SimpleManager from './SimpleManager';

const COLORS = ['#a855f7', '#2dd4bf', '#fb7185', '#3b82f6', '#f59e0b'];

const Dashboard: React.FC = () => {
  const { user, signOut } = useAuth();
  const [activeTab, setActiveTab] = useState<'stats' | 'games' | 'genres' | 'platforms'>('stats');
  const [stats, setStats] = useState({
    totalJogos: 0,
    totalGeneros: 0,
    totalPlataformas: 0,
    avgNota: 0,
  });
  const [chartData, setChartData] = useState<any[]>([]);

  useEffect(() => {
    async function loadStats() {
      try {
        const [jogos, generos, plataformas] = await Promise.all([
          api.get('/gamevault/jogo'),
          api.get('/gamevault/genero'),
          api.get('/gamevault/plataforma'),
        ]);

        const totalJogos = jogos.data.length;
        const totalGeneros = generos.data.length;
        const totalPlataformas = plataformas.data.length;
        const avgNota = totalJogos > 0 
          ? jogos.data.reduce((acc: number, j: any) => acc + j.nota, 0) / totalJogos 
          : 0;

        setStats({ totalJogos, totalGeneros, totalPlataformas, avgNota });

        // Agrupar por plataforma para o gráfico
        const platMap: any = {};
        jogos.data.forEach((j: any) => {
          j.plataformas.forEach((p: any) => {
            platMap[p.nome] = (platMap[p.nome] || 0) + 1;
          });
        });

        const platData = Object.keys(platMap).map(name => ({
          name,
          value: platMap[name],
        }));

        setChartData(platData);
      } catch (error) {
        console.error('Erro ao carregar estatísticas', error);
      }
    }

    loadStats();
  }, []);

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <Gamepad2 color="#8b5cf6" size={32} />
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
            <Layers size={20} /> Gêneros
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
              <h1>Olá, {user?.nome}</h1>
              <p>Aqui está o resumo da sua coleção.</p>
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
                  <p>Gêneros</p>
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
                  <h3>{stats.avgNota.toFixed(1)}</h3>
                  <p>Média de Notas</p>
                </div>
              </div>
            </div>

            <div className="charts-grid">
              <div className="chart-container">
                <h3>Jogos por Plataforma</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <PieChart>
                    <Pie
                      data={chartData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={80}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {chartData.map((_, index) => (
                        <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              <div className="chart-container">
                <h3>Distribuição</h3>
                <ResponsiveContainer width="100%" height={300}>
                  <BarChart data={chartData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
                    <XAxis dataKey="name" stroke="#94a3b8" />
                    <YAxis stroke="#94a3b8" />
                    <Tooltip />
                    <Bar dataKey="value" fill="#8b5cf6" radius={[4, 4, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </>
        )}

        {activeTab === 'games' && <Catalog />}
        {activeTab === 'genres' && <SimpleManager type="genero" title="Gêneros" />}
        {activeTab === 'platforms' && <SimpleManager type="plataforma" title="Plataformas" />}
      </main>
    </div>
  );
};

export default Dashboard;
