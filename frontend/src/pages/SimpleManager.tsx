import React, { useCallback, useEffect, useState } from 'react';
import api from '../services/api';
import { getApiErrorMessage } from '../services/apiError';
import { useToast } from '../contexts/ToastContext';
import { FolderOpen, Plus, Trash2 } from 'lucide-react';
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

const TYPE_LABEL: Record<SimpleManagerProps['type'], string> = {
  genero: 'genero',
  plataforma: 'plataforma',
};

const SimpleManager: React.FC<SimpleManagerProps> = ({ type, title }) => {
  const [items, setItems] = useState<ManagedItem[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [pendingDelete, setPendingDelete] = useState<ManagedItem | null>(null);
  const [nome, setNome] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(true);
  const { showToast } = useToast();

  const singularLabel = TYPE_LABEL[type];

  const loadData = useCallback(async () => {
    setLoading(true);
    const res = await api.get(`/gamevault/${type}`);
    setItems(res.data);
    setLoading(false);
  }, [type]);

  useEffect(() => {
    let mounted = true;
    setLoading(true);

    api.get(`/gamevault/${type}`)
      .then((res) => {
        if (mounted) {
          setItems(res.data);
        }
      })
      .catch((error) => {
        if (mounted) {
          showToast({
            title: `Nao foi possivel carregar ${title.toLowerCase()}.`,
            message: getApiErrorMessage(error, 'Atualize a pagina e tente novamente.'),
            variant: 'error',
          });
        }
      })
      .finally(() => {
        if (mounted) {
          setLoading(false);
        }
      });

    return () => {
      mounted = false;
    };
  }, [showToast, title, type]);

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
        title: `Nao foi possivel salvar ${singularLabel}.`,
        message: getApiErrorMessage(error, `Verifique o nome informado para ${singularLabel}.`),
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
        title: `Nao foi possivel excluir ${singularLabel}.`,
        message: getApiErrorMessage(error, 'Existem jogos vinculados a este item.'),
        variant: 'error',
      });
    }
  }

  return (
    <div className="catalog-container">
      <header className="catalog-header">
        <div>
          <h2 style={{ fontSize: '2rem', fontWeight: 800 }}>{title}</h2>
          <p style={{ color: 'var(--text-muted)', marginTop: '0.35rem' }}>
            Organize os {title.toLowerCase()} usados na sua biblioteca.
          </p>
        </div>
        <button className="btn-add" onClick={() => setIsModalOpen(true)}>
          <Plus size={20} /> Novo(a) {singularLabel}
        </button>
      </header>

      {loading ? (
        <div className="games-grid">
          {Array.from({ length: 4 }).map((_, index) => (
            <div key={index} className="game-card game-card-skeleton">
              <div className="game-info">
                <div className="skeleton-line short" />
                <div className="skeleton-line medium" />
              </div>
            </div>
          ))}
        </div>
      ) : items.length === 0 ? (
        <div className="empty-catalog">
          <FolderOpen size={30} />
          <h3>Nenhum item cadastrado</h3>
          <p>Crie o primeiro {singularLabel} para começar a organizar melhor os seus jogos.</p>
        </div>
      ) : (
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
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={`Adicionar ${title}`}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor={`novo-${type}`}>Nome</label>
            <input id={`novo-${type}`} value={nome} onChange={(e) => setNome(e.target.value)} required />
          </div>
          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-save" disabled={submitting}>{submitting ? 'Salvando...' : 'Salvar'}</button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        isOpen={!!pendingDelete}
        title={`Excluir ${singularLabel}`}
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
