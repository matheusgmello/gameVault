import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import Login from './Login';
import { ToastProvider } from '../contexts/ToastContext';

const signInMock = vi.fn();
const navigateMock = vi.fn();

vi.mock('../contexts/AuthContext', () => ({
  useAuth: () => ({
    signIn: signInMock,
  }),
}));

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom');
  return {
    ...actual,
    useNavigate: () => navigateMock,
  };
});

describe('Login', () => {
  beforeEach(() => {
    signInMock.mockReset();
    navigateMock.mockReset();
  });

  it('realiza login e navega para o dashboard', async () => {
    signInMock.mockResolvedValue(undefined);
    const user = userEvent.setup();

    render(
      <ToastProvider>
        <MemoryRouter>
          <Login />
        </MemoryRouter>
      </ToastProvider>,
    );

    await user.type(screen.getByLabelText(/e-mail/i), 'matheus@email.com');
    await user.type(screen.getByLabelText(/senha/i), '12345');
    await user.click(screen.getByRole('button', { name: /entrar/i }));

    await waitFor(() => {
      expect(signInMock).toHaveBeenCalledWith('matheus@email.com', '12345');
      expect(navigateMock).toHaveBeenCalledWith('/');
    });
  });
});
