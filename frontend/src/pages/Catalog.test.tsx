import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import Catalog from './Catalog';
import { ToastProvider } from '../contexts/ToastContext';
import api from '../services/api';

vi.mock('../services/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}));

const mockedApi = vi.mocked(api);

const jogos = Array.from({ length: 7 }).map((_, index) => ({
  id: index + 1,
  titulo: `Jogo ${index + 1}`,
  descricao: 'Descricao',
  dataLancamento: '2024-01-01',
  capaUrl: '',
  nota: 8,
  status: 'JOGANDO',
  favorito: false,
  review: '',
  horasJogadas: 10,
  generos: [{ id: 1, nome: 'RPG' }],
  plataformas: [{ id: 1, nome: 'PC' }],
}));

describe('Catalog', () => {
  beforeEach(() => {
    mockedApi.get.mockReset();
    mockedApi.post.mockReset();
    mockedApi.delete.mockReset();

    mockedApi.get.mockImplementation((url: string) => {
      if (url === '/gamevault/genero') {
        return Promise.resolve({ data: [{ id: 1, nome: 'RPG' }] });
      }

      if (url === '/gamevault/plataforma') {
        return Promise.resolve({ data: [{ id: 1, nome: 'PC' }] });
      }

      return Promise.resolve({ data: jogos });
    });
  });

  it('exibe paginacao e abre confirmacao de exclusao', async () => {
    const user = userEvent.setup();

    render(
      <ToastProvider>
        <Catalog />
      </ToastProvider>,
    );

    expect(await screen.findByText(/pagina 1 de 2/i)).toBeInTheDocument();
    expect(screen.getByText('Jogo 1')).toBeInTheDocument();
    expect(screen.queryByText('Jogo 7')).not.toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: /proxima pagina/i }));

    await waitFor(() => {
      expect(screen.getByText('Jogo 7')).toBeInTheDocument();
    });

    const deleteButtons = screen.getAllByLabelText(/excluir jogo/i);
    await user.click(deleteButtons[0]);

    expect(await screen.findByText(/tem certeza que deseja excluir/i)).toBeInTheDocument();
  });
});
