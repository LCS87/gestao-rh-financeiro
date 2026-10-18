import { describe, expect, it } from 'vitest';
import { calcularLiquido, formatBRL, percentualHorasExtras } from './format';

describe('formatBRL', () => {
  it('formata valores monetarios em pt-BR', () => {
    expect(formatBRL(1234.5).replace(/\u00a0/g,' ')).toBe('R$ 1.234,50');
  });
  it('trata null/undefined como zero', () => {
    expect(formatBRL(null).replace(/\u00a0/g,' ')).toBe('R$ 0,00');
    expect(formatBRL(undefined).replace(/\u00a0/g,' ')).toBe('R$ 0,00');
  });
});

describe('calcularLiquido (holerite)', () => {
  it('subtrai descontos dos proventos', () => {
    expect(calcularLiquido(5000, 1200.35)).toBe(3799.65);
  });
  it('arredonda para 2 casas', () => {
    expect(calcularLiquido(3000.123, 1000.001)).toBe(2000.12);
  });
  it('tolera valores ausentes', () => {
    expect(calcularLiquido(undefined, undefined)).toBe(0);
    expect(calcularLiquido(2500, null)).toBe(2500);
  });
});

describe('percentualHorasExtras', () => {
  it('calcula o percentual com uma casa decimal', () => {
    expect(percentualHorasExtras(200, 20)).toBe(10);
    expect(percentualHorasExtras(220, 15)).toBe(6.8);
  });
  it('retorna 0 quando nao ha horas normais', () => {
    expect(percentualHorasExtras(0, 10)).toBe(0);
  });
});
