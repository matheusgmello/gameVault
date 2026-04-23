import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Clock, Heart, Plus, Search, Trash2 } from 'lucide-react';
import Modal from '../components/Modal';
import '../styles/Catalog.css';

type GameStatus = 'WISHLIST' | 'JOGANDO' | 'ZERADO' | 'ABANDONADO';

const STATUS_LABELS: Record<GameStatus, string> = {
  WISHLIST: 'Wishlist',
  JOGANDO: 'Jogando',
  ZERADO: 'Zerado',
  ABANDONADO: 'Abandonado',
};

interface GeneroResponse {
  id: number;
  nome: string;
}

interface PlataformaResponse {
  id: number;
  nome: string;
}

interface JogoResponse {
  id: number;
  titulo: string;
  descricao?: string;
  dataLancamento?: string;
  capaUrl?: string;
  nota: number;
  status: GameStatus;
  favorito: boolean;
  review?: string;
  horasJogadas: number;
  generos: GeneroResponse[];
  plataformas: PlataformaResponse[];
}

const Catalog: React.FC = () => {
  const [jogos, setJogos] = useState<JogoResponse[]>([]);
  const [generos, setGeneros] = useState<GeneroResponse[]>([]);
  const [plataformas, setPlataformas] = useState<PlataformaResponse[]>([]);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Form State
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [capaUrl, setCapaUrl] = useState('');
  const [nota, setNota] = useState(5);
  const [dataLancamento, setDataLancamento] = useState('');
  const [status, setStatus] = useState<GameStatus>('WISHLIST');
  const [favorito, setFavorito] = useState(false);
  const [review, setReview] = useState('');
  const [horasJogadas, setHorasJogadas] = useState(0);
  const [selectedGeneros, setSelectedGeneros] = useState<number[]>([]);
  const [selectedPlataformas, setSelectedPlataformas] = useState<number[]>([]);

  const loadData = React.useCallback(async () => {
    const [resJogos, resGeneros, resPlats] = await Promise.all([
      api.get('/gamevault/jogo'),
      api.get('/gamevault/genero'),
      api.get('/gamevault/plataforma'),
    ]);
    setJogos(resJogos.data);
    setGeneros(resGeneros.data);
    setPlataformas(resPlats.data);
  }, []);

  useEffect(() => {
    let mounted = true;

    Promise.all([
      api.get('/gamevault/jogo'),
      api.get('/gamevault/genero'),
      api.get('/gamevault/plataforma'),
    ]).then(([resJogos, resGeneros, resPlats]) => {
      if (!mounted) {
        return;
      }

      setJogos(resJogos.data);
      setGeneros(resGeneros.data);
      setPlataformas(resPlats.data);
    });

    return () => {
      mounted = false;
    };
  }, []);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await api.post('/gamevault/jogo', {
        titulo,
        descricao,
        capaUrl,
        nota,
        dataLancamento,
        status,
        favorito,
        review,
        horasJogadas,
        generos: selectedGeneros,
        plataformas: selectedPlataformas
      });
      setIsModalOpen(false);
      void loadData();
      resetForm();
    } catch {
      alert('Erro ao salvar jogo');
    }
  }

  async function handleDelete(id: number) {
    if (window.confirm('Deseja realmente excluir este jogo?')) {
      await api.delete(`/gamevault/jogo/${id}`);
      void loadData();
    }
  }

  function resetForm() {
    setTitulo('');
    setDescricao('');
    setCapaUrl('');
    setNota(5);
    setDataLancamento('');
    setStatus('WISHLIST');
    setFavorito(false);
    setReview('');
    setHorasJogadas(0);
    setSelectedGeneros([]);
    setSelectedPlataformas([]);
  }

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
        <button className="btn-add" onClick={() => setIsModalOpen(true)}>
          <Plus size={20} /> Novo Jogo
        </button>
      </header>

      <div className="games-grid">
        {filteredJogos.map(jogo => (
          <div key={jogo.id} className="game-card">
            <div className="game-cover">
              {jogo.capaUrl ? (
                <img className="game-cover-image" src={jogo.capaUrl} alt={`Capa de ${jogo.titulo}`} />
              ) : (
                <div className="placeholder-cover">
                  {jogo.titulo.substring(0, 2).toUpperCase()}
                </div>
              )}
              <div className="game-badge">{jogo.nota.toFixed(1)}</div>
              {jogo.favorito && (
                <div className="favorite-badge">
                  <Heart size={16} fill="currentColor" />
                </div>
              )}
              <button className="btn-delete-game" onClick={() => handleDelete(jogo.id)}>
                <Trash2 size={18} />
              </button>
            </div>
            <div className="game-info">
              <div className="game-meta-row">
                <span className={`status-chip status-${jogo.status.toLowerCase()}`}>
                  {STATUS_LABELS[jogo.status]}
                </span>
                <span className="hours-chip">
                  <Clock size={14} /> {jogo.horasJogadas}h
                </span>
              </div>
              <h3>{jogo.titulo}</h3>
              <p>{jogo.generos.map((g) => g.nome).join(', ')}</p>
              {jogo.review && <p className="game-review">{jogo.review}</p>}
              <div className="game-platforms">
                {jogo.plataformas.map((p) => (
                  <span key={p.id} className="btn-tag">{p.nome}</span>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title="Adicionar Novo Jogo"
      >
        <form onSubmit={handleSubmit} className="game-form">
          <div className="form-group">
            <label>Título</label>
            <input value={titulo} onChange={e => setTitulo(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Descrição</label>
            <textarea value={descricao} onChange={e => setDescricao(e.target.value)} rows={3} />
          </div>
          <div className="form-group">
            <label>URL da capa</label>
            <input value={capaUrl} onChange={e => setCapaUrl(e.target.value)} placeholder="https://..." />
          </div>
          <div className="form-group">
            <label>Review pessoal</label>
            <textarea value={review} onChange={e => setReview(e.target.value)} rows={3} />
          </div>
          <div className="form-row" style={{ display: 'flex', gap: '1rem' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Nota (0-10)</label>
              <input type="number" step="0.1" min="0" max="10" value={nota} onChange={e => setNota(Number(e.target.value))} required />
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Lançamento</label>
              <input type="date" value={dataLancamento} onChange={e => setDataLancamento(e.target.value)} required />
            </div>
          </div>
          <div className="form-row" style={{ display: 'flex', gap: '1rem' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Status</label>
              <select value={status} onChange={e => setStatus(e.target.value as GameStatus)}>
                <option value="WISHLIST">Wishlist</option>
                <option value="JOGANDO">Jogando</option>
                <option value="ZERADO">Zerado</option>
                <option value="ABANDONADO">Abandonado</option>
              </select>
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label>Horas jogadas</label>
              <input type="number" min="0" value={horasJogadas} onChange={e => setHorasJogadas(Number(e.target.value))} />
            </div>
          </div>
          <label className="favorite-toggle">
            <input
              type="checkbox"
              checked={favorito}
              onChange={e => setFavorito(e.target.checked)}
            />
            Marcar como favorito
          </label>
          
          <div className="form-group">
            <label>Gêneros</label>
            <div className="checkbox-grid">
              {generos.map(g => (
                <label key={g.id} className="checkbox-item">
                  <input 
                    type="checkbox" 
                    checked={selectedGeneros.includes(g.id)}
                    onChange={e => {
                      if (e.target.checked) setSelectedGeneros([...selectedGeneros, g.id]);
                      else setSelectedGeneros(selectedGeneros.filter(id => id !== g.id));
                    }}
                  />
                  {g.nome}
                </label>
              ))}
            </div>
          </div>

          <div className="form-group">
            <label>Plataformas</label>
            <div className="checkbox-grid">
              {plataformas.map(p => (
                <label key={p.id} className="checkbox-item">
                  <input 
                    type="checkbox" 
                    checked={selectedPlataformas.includes(p.id)}
                    onChange={e => {
                      if (e.target.checked) setSelectedPlataformas([...selectedPlataformas, p.id]);
                      else setSelectedPlataformas(selectedPlataformas.filter(id => id !== p.id));
                    }}
                  />
                  {p.nome}
                </label>
              ))}
            </div>
          </div>

          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-save">Salvar Jogo</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Catalog;
