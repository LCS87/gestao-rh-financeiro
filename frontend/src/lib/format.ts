const brl = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

/** Formata um valor monetário em BRL (pt-BR). Null/undefined viram R$ 0,00. */
export function formatBRL(valor?: number | null): string {
  return brl.format(valor ?? 0);
}

/** Calcula o líquido da folha: proventos - descontos (nunca negativo por padrão). */
export function calcularLiquido(proventos?: number | null, descontos?: number | null): number {
  const liq = (proventos ?? 0) - (descontos ?? 0);
  return Math.round(liq * 100) / 100;
}

/** Percentual de horas extras sobre horas normais (0 quando não há horas normais). */
export function percentualHorasExtras(horasNormais: number, horasExtras: number): number {
  if (!horasNormais || horasNormais <= 0) return 0;
  return Math.round((horasExtras / horasNormais) * 1000) / 10;
}
