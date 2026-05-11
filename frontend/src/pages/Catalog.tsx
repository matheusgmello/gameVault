import React, { useCallback, useEffect, useMemo, useState } from 'react';
import api from '../services/api';
import { getApiErrorMessage } from '../services/apiError';
import { useToast } from '../contexts/ToastContext';
import { Calendar, ChevronLeft, ChevronRight, Clock, Eye, Heart, Plus, Search, SlidersHorizontal, Star, Trash2, X } from 'lucide-react';
import Modal from '../components/Modal';
import ConfirmDialog from '../components/ConfirmDialog';
import '../styles/Catalog.css';

type GameStatus = 'WISHLIST' | 'JOGANDO' | 'ZERADO' | 'ABANDONADO';
type SortOption = 'titulo' | 'nota' | 'lancamento' | 'horas';

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

interface GameFilters {
  busca?: string;
  status?: GameStatus;
  generoId?: number;
  plataformaId?: number;
  favorito?: boolean;
  ordenarPor?: SortOption;
}

const ITEMS_PER_PAGE = 6;

const Catalog: React.FC = () => {
  const [jogos, setJogos] = useState<JogoResponse[]>([]);
  const [generos, setGeneros] = useState<GeneroResponse[]>([]);
  const [plataformas, setPlataformas] = useState<PlataformaResponse[]>([]);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<GameStatus | ''>('');
  const [genreFilter, setGenreFilter] = useState('');
  const [platformFilter, setPlatformFilter] = useState('');
  const [favoriteFilter, setFavoriteFilter] = useState<'all' | 'favorites'>('all');
  const [sortBy, setSortBy] = useState<SortOption>('titulo');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedGame, setSelectedGame] = useState<JogoResponse | null>(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [pendingDelete, setPendingDelete] = useState<JogoResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);

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
  const { showToast } = useToast();

  const currentFilters = useCallback(() => {
    const filters: GameFilters = { ordenarPor: sortBy };

    if (search.trim()) filters.busca = search.trim();
    if (statusFilter) filters.status = statusFilter;
    if (genreFilter) filters.generoId = Number(genreFilter);
    if (platformFilter) filters.plataformaId = Number(platformFilter);
    if (favoriteFilter === 'favorites') filters.favorito = true;

    return filters;
  }, [favoriteFilter, genreFilter, platformFilter, search, sortBy, statusFilter]);

  const loadData = useCallback(async () => {
    setLoading(true);
    const [resJogos, resGeneros, resPlats] = await Promise.all([
      api.get('/gamevault/jogo', { params: currentFilters() }),
      api.get('/gamevault/genero'),
      api.get('/gamevault/plataforma'),
    ]);
    setJogos(resJogos.data);
    setGeneros(resGeneros.data);
    setPlataformas(resPlats.data);
    setLoading(false);
  }, [currentFilters]);

  useEffect(() => {
    let mounted = true;

    setLoading(true);
    Promise.all([
      api.get('/gamevault/jogo', { params: currentFilters() }),
      api.get('/gamevault/genero'),
      api.get('/gamevault/plataforma'),
    ])
      .then(([resJogos, resGeneros, resPlats]) => {
        if (!mounted) return;
        setJogos(resJogos.data);
        setGeneros(resGeneros.data);
        setPlataformas(resPlats.data);
        setCurrentPage(1);
      })
      .catch((error) => {
        if (!mounted) return;
        showToast({
          title: 'Nao foi possivel carregar o catalogo.',
          message: getApiErrorMessage(error, 'Atualize a pagina e tente novamente.'),
          variant: 'error',
        });
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, [currentFilters, showToast]);

  useEffect(() => {
    setCurrentPage(1);
  }, [search, statusFilter, genreFilter, platformFilter, favoriteFilter, sortBy]);

  const totalPages = Math.max(1, Math.ceil(jogos.length / ITEMS_PER_PAGE));
  const paginatedJogos = useMemo(() => {
    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    return jogos.slice(start, start + ITEMS_PER_PAGE);
  }, [currentPage, jogos]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
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
        plataformas: selectedPlataformas,
      });
      setIsModalOpen(false);
      resetForm();
      showToast({ title: 'Jogo salvo com sucesso.', variant: 'success' });
      void loadData();
    } catch (error) {
      showToast({
        title: 'Nao foi possivel salvar o jogo.',
        message: getApiErrorMessage(error, 'Revise os dados preenchidos e tente novamente.'),
        variant: 'error',
      });
    } finally {
      setSubmitting(false);
    }
  }

  async function confirmDelete() {
    if (!pendingDelete) return;
    try {
      await api.delete(`/gamevault/jogo/${pendingDelete.id}`);
      setIsDetailOpen(false);
      setSelectedGame(null);
      setPendingDelete(null);
      showToast({ title: 'Jogo excluido com sucesso.', variant: 'success' });
      void loadData();
    } catch (error) {
      showToast({
        title: 'Nao foi possivel excluir o jogo.',
        message: getApiErrorMessage(error, 'Tente novamente em instantes.'),
        variant: 'error',
      });
    }
  }

  async function handleOpenDetail(id: number) {
    try {
      const response = await api.get(`/gamevault/jogo/${id}`);
      setSelectedGame(response.data);
      setIsDetailOpen(true);
    } catch (error) {
      showToast({
        title: 'Nao foi possivel abrir o jogo.',
        message: getApiErrorMessage(error, 'Tente novamente em instantes.'),
        variant: 'error',
      });
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

  function clearFilters() {
    setSearch('');
    setStatusFilter('');
    setGenreFilter('');
    setPlatformFilter('');
    setFavoriteFilter('all');
    setSortBy('titulo');
  }

  const hasFilters = Boolean(search || statusFilter || genreFilter || platformFilter || favoriteFilter !== 'all' || sortBy !== 'titulo');

  return (
    <div className="catalog-container">
      <header className="catalog-header">
        <div className="search-bar">
          <Search size={20} />
          <input
            type="text"
            placeholder="Pesquisar jogos..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <button className="btn-add" onClick={() => setIsModalOpen(true)}>
          <Plus size={20} /> Novo Jogo
        </button>
      </header>

      <section className="catalog-filters" aria-label="Filtros do catalogo">
        <div className="filters-title">
          <SlidersHorizontal size={18} />
          <span>Filtros</span>
        </div>
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value as GameStatus | '')}>
          <option value="">Todos os status</option>
          <option value="WISHLIST">Wishlist</option>
          <option value="JOGANDO">Jogando</option>
          <option value="ZERADO">Zerado</option>
          <option value="ABANDONADO">Abandonado</option>
        </select>
        <select value={genreFilter} onChange={(e) => setGenreFilter(e.target.value)}>
          <option value="">Todos os generos</option>
          {generos.map((g) => (
            <option key={g.id} value={g.id}>{g.nome}</option>
          ))}
        </select>
        <select value={platformFilter} onChange={(e) => setPlatformFilter(e.target.value)}>
          <option value="">Todas as plataformas</option>
          {plataformas.map((p) => (
            <option key={p.id} value={p.id}>{p.nome}</option>
          ))}
        </select>
        <select value={favoriteFilter} onChange={(e) => setFavoriteFilter(e.target.value as 'all' | 'favorites')}>
          <option value="all">Todos</option>
          <option value="favorites">Favoritos</option>
        </select>
        <select value={sortBy} onChange={(e) => setSortBy(e.target.value as SortOption)}>
          <option value="titulo">Ordenar por titulo</option>
          <option value="nota">Maior nota</option>
          <option value="lancamento">Mais recentes</option>
          <option value="horas">Mais jogados</option>
        </select>
        {hasFilters && (
          <button className="btn-clear-filters" onClick={clearFilters}>
            <X size={16} /> Limpar
          </button>
        )}
      </section>

      <div className="catalog-toolbar">
        <div className="catalog-result-count">
          {jogos.length} {jogos.length === 1 ? 'jogo encontrado' : 'jogos encontrados'}
        </div>
        {jogos.length > ITEMS_PER_PAGE && (
          <div className="pagination">
            <button type="button" aria-label="Pagina anterior" className="btn-page" disabled={currentPage === 1} onClick={() => setCurrentPage((page) => page - 1)}>
              <ChevronLeft size={16} />
            </button>
            <span className="pagination-label">Pagina {currentPage} de {totalPages}</span>
            <button type="button" aria-label="Proxima pagina" className="btn-page" disabled={currentPage === totalPages} onClick={() => setCurrentPage((page) => page + 1)}>
              <ChevronRight size={16} />
            </button>
          </div>
        )}
      </div>

      {loading ? (
        <div className="games-grid">
          {Array.from({ length: ITEMS_PER_PAGE }).map((_, index) => (
            <div key={index} className="game-card game-card-skeleton">
              <div className="game-cover skeleton-block" />
              <div className="game-info">
                <div className="skeleton-line short" />
                <div className="skeleton-line medium" />
                <div className="skeleton-line long" />
              </div>
            </div>
          ))}
        </div>
      ) : jogos.length === 0 ? (
        <div className="empty-catalog">
          <Search size={32} />
          <h3>Nenhum jogo encontrado</h3>
          <p>Ajuste os filtros ou cadastre um novo jogo para preencher sua biblioteca.</p>
        </div>
      ) : (
        <div className="games-grid">
          {paginatedJogos.map((jogo) => (
            <article key={jogo.id} className="game-card" onClick={() => void handleOpenDetail(jogo.id)}>
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
                <button
                  className="btn-delete-game"
                  aria-label={`Excluir ${jogo.titulo}`}
                  onClick={(e) => {
                    e.stopPropagation();
                    setPendingDelete(jogo);
                  }}
                >
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
                <span className="game-detail-hint">
                  <Eye size={15} /> Ver detalhes
                </span>
              </div>
            </article>
          ))}
        </div>
      )}

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Adicionar Novo Jogo"
      >
        <form onSubmit={handleSubmit} className="game-form">
          <div className="form-group">
            <label htmlFor="jogo-titulo">Titulo</label>
            <input id="jogo-titulo" value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
          </div>
          <div className="form-group">
            <label htmlFor="jogo-descricao">Descricao</label>
            <textarea id="jogo-descricao" value={descricao} onChange={(e) => setDescricao(e.target.value)} rows={3} />
          </div>
          <div className="form-group">
            <label htmlFor="jogo-capa">URL da capa</label>
            <input id="jogo-capa" value={capaUrl} onChange={(e) => setCapaUrl(e.target.value)} placeholder="https://..." />
          </div>
          <div className="form-group">
            <label htmlFor="jogo-review">Review pessoal</label>
            <textarea id="jogo-review" value={review} onChange={(e) => setReview(e.target.value)} rows={3} />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="jogo-nota">Nota (0-10)</label>
              <input id="jogo-nota" type="number" step="0.1" min="0" max="10" value={nota} onChange={(e) => setNota(Number(e.target.value))} required />
            </div>
            <div className="form-group">
              <label htmlFor="jogo-lancamento">Lancamento</label>
              <input id="jogo-lancamento" type="date" value={dataLancamento} onChange={(e) => setDataLancamento(e.target.value)} required />
            </div>
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="jogo-status">Status</label>
              <select id="jogo-status" value={status} onChange={(e) => setStatus(e.target.value as GameStatus)}>
                <option value="WISHLIST">Wishlist</option>
                <option value="JOGANDO">Jogando</option>
                <option value="ZERADO">Zerado</option>
                <option value="ABANDONADO">Abandonado</option>
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="jogo-horas">Horas jogadas</label>
              <input id="jogo-horas" type="number" min="0" value={horasJogadas} onChange={(e) => setHorasJogadas(Number(e.target.value))} />
            </div>
          </div>
          <label className="favorite-toggle">
            <input type="checkbox" checked={favorito} onChange={(e) => setFavorito(e.target.checked)} />
            Marcar como favorito
          </label>

          <div className="form-group">
            <label>Generos</label>
            <div className="checkbox-grid">
              {generos.map((g) => (
                <label key={g.id} className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={selectedGeneros.includes(g.id)}
                    onChange={(e) => {
                      if (e.target.checked) setSelectedGeneros([...selectedGeneros, g.id]);
                      else setSelectedGeneros(selectedGeneros.filter((id) => id !== g.id));
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
              {plataformas.map((p) => (
                <label key={p.id} className="checkbox-item">
                  <input
                    type="checkbox"
                    checked={selectedPlataformas.includes(p.id)}
                    onChange={(e) => {
                      if (e.target.checked) setSelectedPlataformas([...selectedPlataformas, p.id]);
                      else setSelectedPlataformas(selectedPlataformas.filter((id) => id !== p.id));
                    }}
                  />
                  {p.nome}
                </label>
              ))}
            </div>
          </div>

          <div className="form-actions">
            <button type="button" className="btn-cancel" onClick={() => setIsModalOpen(false)}>Cancelar</button>
            <button type="submit" className="btn-save" disabled={submitting}>{submitting ? 'Salvando...' : 'Salvar Jogo'}</button>
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={isDetailOpen}
        onClose={() => setIsDetailOpen(false)}
        title={selectedGame?.titulo ?? 'Detalhes do jogo'}
      >
        {selectedGame && (
          <div className="game-detail">
            <div className="detail-cover">
              {selectedGame.capaUrl ? (
                <img src={selectedGame.capaUrl} alt={`Capa de ${selectedGame.titulo}`} />
              ) : (
                <div className="placeholder-cover">
                  {selectedGame.titulo.substring(0, 2).toUpperCase()}
                </div>
              )}
            </div>

            <div className="detail-summary">
              <span className={`status-chip status-${selectedGame.status.toLowerCase()}`}>
                {STATUS_LABELS[selectedGame.status]}
              </span>
              <span className="detail-metric">
                <Star size={16} /> {selectedGame.nota.toFixed(1)}
              </span>
              <span className="detail-metric">
                <Clock size={16} /> {selectedGame.horasJogadas}h
              </span>
              {selectedGame.dataLancamento && (
                <span className="detail-metric">
                  <Calendar size={16} /> {new Date(`${selectedGame.dataLancamento}T00:00:00`).toLocaleDateString('pt-BR')}
                </span>
              )}
            </div>

            {selectedGame.descricao && (
              <section className="detail-section">
                <h4>Descricao</h4>
                <p>{selectedGame.descricao}</p>
              </section>
            )}

            {selectedGame.review && (
              <section className="detail-section">
                <h4>Review pessoal</h4>
                <p>{selectedGame.review}</p>
              </section>
            )}

            <section className="detail-section">
              <h4>Generos</h4>
              <div className="game-platforms">
                {selectedGame.generos.map((g) => (
                  <span key={g.id} className="btn-tag">{g.nome}</span>
                ))}
              </div>
            </section>

            <section className="detail-section">
              <h4>Plataformas</h4>
              <div className="game-platforms">
                {selectedGame.plataformas.map((p) => (
                  <span key={p.id} className="btn-tag">{p.nome}</span>
                ))}
              </div>
            </section>

            <div className="form-actions">
              <button type="button" className="btn-cancel" onClick={() => setIsDetailOpen(false)}>Fechar</button>
              <button type="button" className="btn-danger" onClick={() => setPendingDelete(selectedGame)}>
                Excluir jogo
              </button>
            </div>
          </div>
        )}
      </Modal>

      <ConfirmDialog
        isOpen={!!pendingDelete}
        title="Excluir jogo"
        description={`Tem certeza que deseja excluir ${pendingDelete?.titulo ?? 'este jogo'} da sua biblioteca?`}
        confirmLabel="Excluir jogo"
        onConfirm={() => void confirmDelete()}
        onCancel={() => setPendingDelete(null)}
        danger
      />
    </div>
  );
};

export default Catalog;
