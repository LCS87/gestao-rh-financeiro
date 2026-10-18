import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { StatCard } from './StatCard';

describe('StatCard', () => {
  it('exibe valor e label', () => {
    render(
      <StatCard
        icon={<span data-testid="icon">i</span>}
        value="247"
        label="Colaboradores ativos"
        gradient="from-blue-500 to-cyan-500"
        hoverBorder=""
      />,
    );
    expect(screen.getByText('247')).toBeInTheDocument();
    expect(screen.getByText('Colaboradores ativos')).toBeInTheDocument();
  });

  it('exibe trend quando fornecida', () => {
    render(
      <StatCard
        icon={<span>i</span>}
        value="2.3%"
        label="Turnover"
        trend="-0.4% vs mes anterior"
        gradient="from-red-500 to-orange-500"
        hoverBorder=""
      />,
    );
    expect(screen.getByText('-0.4% vs mes anterior')).toBeInTheDocument();
  });
});
