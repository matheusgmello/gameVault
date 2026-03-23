import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { Plus, Trash2 } from 'lucide-react';
import Modal from '../components/Modal';

interface SimpleManagerProps {
  type: 'genero' | 'plataforma';
  title: string;
}

const SimpleManager: React.FC<SimpleManagerProps> = ({ type, title }) => {
  const [items, setItems] = useState<any[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [nome, setNome] = useState('');

  useEffect(() => {
    loadData();
  }, [type]);

  async function loadData() {
    const res = await api.get(`/gamevault/${type}`);
    setItems(res.data);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await api.post(`/gamevault/${type}`, { nome });
      setIsModalOpen(false);
      setNome('');
      loadData();
    } catch (error) {
      alert(`Erro ao salvar ${type}`);
    }
  }

  async function handleDelete(id: number) {
    if (window.confirm(`Deseja excluir este(a) ${type}?`)) {
      try {
        await api.delete(`/gamevault/${type}/${id}`);
        loadData();
      } catch (error) {
        alert('Não é possível excluir: existem jogos vinculados.');
      }
    }
  }

  return (
    <div className="catalog-container">
      <header className="catalog-header">
        <h2 style={{ fontSize: '2rem', fontWeight: 800 }}>{title}</h2>
        <button className="btn-add" onClick={() => setIsModalOpen(true)}>
          <Plus size={20} /> Novo(a) {type}
        </button>
      </header>

      <div className="games-grid">
        {items.map(item => (
          <div key={item.id} className="game-card" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '1.25rem', fontWeight: 700 }}>{item.nome}</span>
            <button className="btn-logout" style={{ width: 'auto', padding: '0.5rem' }} onClick={() => handleDelete(item.id)}>
              <Trash2 size={20} />
            </button>
          </div>
        ))}
      </div>

      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title={`Adicionar ${title}`}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Nome</label>
            <input value={nome} onChange={e => setNome(e.target.value)} required />
          </div>
          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-save">Salvar</button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SimpleManager;
