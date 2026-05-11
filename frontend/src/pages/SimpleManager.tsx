import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { getApiErrorMessage } from '../services/apiError';
import { useToast } from '../contexts/ToastContext';
import { Plus, Trash2 } from 'lucide-react';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';

interface SimpleManagerProps {
  type: 'genero' | 'plataforma';
  title: string;
}

interface ManagedItem {
  id: number;
  nome: string;
}

const SimpleManager: React.FC<SimpleManagerProps> = ({ type, title }) => {
  const [items, setItems] = useState<ManagedItem[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [pendingDelete, setPendingDelete] = useState<ManagedItem | null>(null);
  const [nome, setNome] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { showToast } = useToast();

  const loadData = React.useCallback(async () => {
    const res = await api.get(`/gamevault/${type}`);
    setItems(res.data);
  }, [type]);

  useEffect(() => {
    let mounted = true;

    api.get(`/gamevault/${type}`).then((res) => {
      if (mounted) {
        setItems(res.data);
      }
    });

    return () => {
      mounted = false;
    };
  }, [type]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    try {
      await api.post(`/gamevault/${type}`, { nome });
      setIsModalOpen(false);
      setNome('');
      showToast({ title: `${title.slice(0, -1)} salvo com sucesso.`, variant: 'success' });
      void loadData();
    } catch (error) {
      showToast({
        title: `Nao foi possivel salvar ${type}.`,
        message: getApiErrorMessage(error, `Verifique o nome informado para ${type}.`),
        variant: 'error',
      });
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete() {
    if (!pendingDelete) {
      return;
    }

    try {
      await api.delete(`/gamevault/${type}/${pendingDelete.id}`);
      showToast({ title: `${title.slice(0, -1)} excluido com sucesso.`, variant: 'success' });
      setPendingDelete(null);
      void loadData();
    } catch (error) {
      showToast({
        title: `Nao foi possivel excluir ${type}.`,
        message: getApiErrorMessage(error, 'Existem jogos vinculados a este item.'),
        variant: 'error',
      });
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
        {items.map((item) => (
          <div key={item.id} className="game-card" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '1.25rem', fontWeight: 700 }}>{item.nome}</span>
            <button className="btn-logout" style={{ width: 'auto', padding: '0.5rem' }} onClick={() => setPendingDelete(item)}>
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
            <input value={nome} onChange={(e) => setNome(e.target.value)} required />
          </div>
          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-save" disabled={submitting}>{submitting ? 'Salvando...' : 'Salvar'}</button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!pendingDelete}
        title={`Excluir ${type}`}
        description={`Tem certeza que deseja excluir ${pendingDelete?.nome ?? 'este item'}?`}
        confirmLabel="Excluir"
        onConfirm={() => void handleDelete()}
        onCancel={() => setPendingDelete(null)}
        danger
      />
    </div>
  );
};

export default SimpleManager;
