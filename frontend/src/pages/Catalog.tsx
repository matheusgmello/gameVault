import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Search, Plus } from 'lucide-react';
import '../styles/Catalog.css';

const Catalog: React.FC = () => {
  const [jogos, setJogos] = useState<any[]>([]);
  const [search, setSearch] = useState('');

  useEffect(() => {
    api.get('/gamevault/jogo').then(response => {
      setJogos(response.data);
    });
  }, []);

  const filteredJogos = jogos.filter(j => 
    j.titulo.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="catalog-container">
      <header className="catalog-header">
        <div className="search-bar">
          <Search size={20} />
          <input 
            type="text" 
            placeholder="Pesquisar jogos..." 
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
        </div>
        <button className="btn-add">
          <Plus size={20} /> Novo Jogo
        </button>
      </header>

      <div className="games-grid">
        {filteredJogos.map(jogo => (
          <div key={jogo.id} className="game-card">
            <div className="game-cover">
              <div className="placeholder-cover">
                {jogo.titulo.substring(0, 2).toUpperCase()}
              </div>
              <div className="game-badge">{jogo.nota.toFixed(1)}</div>
            </div>
            <div className="game-info">
              <h3>{jogo.titulo}</h3>
              <p>{jogo.generos.map((g: any) => g.nome).join(', ')}</p>
              <div className="game-platforms">
                {jogo.plataformas.map((p: any) => (
                  <span key={p.id} className="platform-tag">{p.nome}</span>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Catalog;
