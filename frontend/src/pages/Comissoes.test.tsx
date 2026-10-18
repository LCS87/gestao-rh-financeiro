import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { describe, expect, it } from 'vitest';
import { Comissoes } from '../pages/Comissoes';
import { ToastProvider } from '../components/Toast';

function renderComissoes() {
  const client = new QueryClient({
    defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
  });
  return render(
    <QueryClientProvider client={client}>
      <ToastProvider>
        <Comissoes />
      </ToastProvider>
    </QueryClientProvider>,
  );
}

// O nome da regra aparece tanto no <option> do simulador quanto na celula da
// tabela; usamos getAllByText + [1] para pegar a celula da tabela.
const celulaRegra = async () => (await screen.findAllByText('Padrão Vendas'))[1];

describe('Comissoes', () => {
  it('carrega regras da API e exibe na tabela', async () => {
    renderComissoes();
    expect(await celulaRegra()).toBeInTheDocument();
    expect(screen.getByText('+20%')).toBeInTheDocument(); // acelerador
    expect(screen.getAllByText('100%').length).toBeGreaterThan(0); // meta minima
  });

  it('abre o modal de nova regra ao clicar em "Nova Regra"', async () => {
    const user = userEvent.setup();
    renderComissoes();
    await celulaRegra();
    await user.click(screen.getByRole('button', { name: /Nova Regra/i }));
    expect(await screen.findByText('Nova Regra de Comissão')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Nome')).toBeInTheDocument();
  });

  it('valida regra sem nome antes de salvar', async () => {
    const user = userEvent.setup();
    renderComissoes();
    await celulaRegra();
    await user.click(screen.getByRole('button', { name: /Nova Regra/i }));
    const dialog = await screen.findByText('Nova Regra de Comissão');
    const modalBtn = await within(dialog.parentElement!.parentElement as HTMLElement).findByRole('button', { name: /Salvar/i });
    await user.click(modalBtn);
    expect(await screen.findByText('Informe o nome da regra.')).toBeInTheDocument();
  });

  it('executa o simulador de comissão contra a API mockada', async () => {
    const user = userEvent.setup();
    renderComissoes();
    await celulaRegra();
    // primeiro combobox = seletor de regra do simulador
    await user.selectOptions(screen.getAllByRole('combobox')[0], 'r1');
    const inputs = screen.getAllByRole('spinbutton');
    await user.clear(inputs[0]);
    await user.type(inputs[0], '1000');
    await user.clear(inputs[1]);
    await user.type(inputs[1], '100');
    await user.click(screen.getByRole('button', { name: /Simular/i }));
    // 1000 * 5% * 1.2 = R$ 60,00
    expect(await screen.findByText(/Comissão simulada: R\$/)).toBeInTheDocument();
    await waitFor(() => expect(screen.getByText(/60,00/)).toBeInTheDocument());
  });
});
