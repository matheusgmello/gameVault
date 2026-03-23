import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Search, Plus, Trash2 } from 'lucide-react';
import Modal from '../components/Modal';
import '../styles/Catalog.css';

const Catalog: React.FC = () => {
  const [jogos, setJogos] = useState<any[]>([]);
  const [generos, setGeneros] = useState<any[]>([]);
  const [plataformas, setPlataformas] = useState<any[]>([]);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  // Form State
  const [titulo, setTitulo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [nota, setNota] = useState(5);
  const [dataLancamento, setDataLancamento] = useState('');
  const [selectedGeneros, setSelectedGeneros] = useState<number[]>([]);
  const [selectedPlataformas, setSelectedPlataformas] = useState<number[]>([]);

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    const [resJogos, resGeneros, resPlats] = await Promise.all([
      api.get('/gamevault/jogo'),
      api.get('/gamevault/genero'),
      api.get('/gamevault/plataforma'),
    ]);
    setJogos(resJogos.data);
    setGeneros(resGeneros.data);
    setPlataformas(resPlats.data);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await api.post('/gamevault/jogo', {
        titulo,
        descricao,
        nota,
        dataLancamento,
        generos: selectedGeneros,
        plataformas: selectedPlataformas
      });
      setIsModalOpen(false);
      loadData();
      resetForm();
    } catch (error) {
      alert('Erro ao salvar jogo');
    }
  }

  async function handleDelete(id: number) {
    if (window.confirm('Deseja realmente excluir este jogo?')) {
      await api.delete(`/gamevault/jogo/${id}`);
      loadData();
    }
  }

  function resetForm() {
    setTitulo('');
    setDescricao('');
    setNota(5);
    setDataLancamento('');
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
              <div className="placeholder-cover">
                {jogo.titulo.substring(0, 2).toUpperCase()}
              </div>
              <div className="game-badge">{jogo.nota.toFixed(1)}</div>
              <button className="btn-delete-game" onClick={() => handleDelete(jogo.id)}>
                <Trash2 size={18} />
              </button>
            </div>
            <div className="game-info">
              <h3>{jogo.titulo}</h3>
              <p>{jogo.generos.map((g: any) => g.nome).join(', ')}</p>
              <div className="game-platforms">
                {jogo.plataformas.map((p: any) => (
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
