import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';

// ===== Fase 4 — Regras de Comissões =====

export interface RegraComissao {
  id: string;
  nome: string;
  descricao?: string;
  tipoComissao: 'PORCENTAGEM_VENDA' | 'VALOR_FIXO' | 'FAIXA_VENDA' | 'BONUS_META';
  porcentagem: number;
  valorFixo?: number;
  valorMinimo?: number;
  valorMaximo?: number;
  aceleradorPercentual?: number;
  metaMinimaPercentual?: number;
  aplicarPorDepartamento: boolean;
  departamentoId?: string;
  ativo: boolean;
}

export interface SimulacaoComissaoResult {
  regraId?: string;
  valorVenda?: number;
  percentualMeta?: number;
  valorComissao?: number;
  [k: string]: unknown;
}

export interface ApuracaoComissao {
  mesReferencia: string;
  totalGeralComissoes: number;
  quantidadeVendas: number;
  funcionarios: Array<{
    funcionarioId: string;
    nomeFuncionario: string;
    departamentoId?: string;
    totalVendas: number;
    totalComissoes: number;
    quantidadeVendas: number;
  }>;
}

export function useRegrasComissao() {
  return useQuery({
    queryKey: ['regras-comissao'],
    queryFn: async () => {
      const { data } = await api.get('/comissoes/regras');
      return data as RegraComissao[];
    },
  });
}

export function useCriarRegraComissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: Partial<RegraComissao>) => {
      const { data } = await api.post('/comissoes/regras', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['regras-comissao'] }),
  });
}

export function useAtualizarRegraComissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...body }: Partial<RegraComissao> & { id: string }) => {
      const { data } = await api.put(`/comissoes/regras/${id}`, body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['regras-comissao'] }),
  });
}

export function useDeletarRegraComissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/comissoes/regras/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['regras-comissao'] }),
  });
}

export function useSimularComissao() {
  return useMutation({
    mutationFn: async ({
      regraId,
      valorVenda,
      percentualMeta,
    }: {
      regraId: string;
      valorVenda: number;
      percentualMeta: number;
    }) => {
      const { data } = await api.post<SimulacaoComissaoResult>(
        `/comissoes/regras/${regraId}/simular`,
        { valorVenda, percentualMeta },
      );
      return data;
    },
  });
}

export function useApuracaoComissoes(mesReferencia: string) {
  return useQuery({
    queryKey: ['apuracao-comissoes', mesReferencia],
    queryFn: async () => {
      const { data } = await api.get<ApuracaoComissao>('/comissoes/apuracao', {
        params: { mesReferencia },
      });
      return data;
    },
    enabled: !!mesReferencia,
  });
}

/**
 * Aplica a apuração de comissões do mês no campo "comissoes" das folhas de
 * pagamento já geradas (POST /financeiro/folha-pagamento/aplicar-comissoes/{mes}).
 */
export function useAplicarComissoesNaFolha() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (mesReferencia: string) => {
      const { data } = await api.post<string>(
        `/financeiro/folha-pagamento/aplicar-comissoes/${mesReferencia}`,
      );
      return data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['apuracao-comissoes'] });
    },
  });
}

// ===== Fase 4 — Central de Relatórios =====

export interface ProvisoesFolha {
  periodo: string;
  quantidadeFolhas: number;
  provisaoFGTS: number;
  provisaoINSS: number;
  provisaoDecimoTerceiro: number;
  provisaoFerias: number;
  comissoesAPagar: number;
  encargosEstimados: number;
  totalProvisionado: number;
  [k: string]: unknown;
}

export interface TurnoverAbsenteismoLinha {
  departamentoId: string;
  totalFuncionarios: number;
  desligadosNoPeriodo: number;
  turnoverPercent: number;
  diasAfastamento: number;
  absenteismoPercent: number;
  [k: string]: unknown;
}

export interface DRELinha {
  periodo: string;
  receitaVendas: number;
  receitaTotal: number;
  custoVendas: number;
  lucroBruto: number;
  despesasTotal: number;
  lucroOperacional: number;
  impostos: number;
  lucroLiquido: number;
  [k: string]: unknown;
}

export function useExtratoProvisoes(periodo: string) {
  return useQuery({
    queryKey: ['provisoes-folha', periodo],
    queryFn: async () => {
      const { data } = await api.get<ProvisoesFolha>('/relatorios/provisoes-folha', {
        params: { periodo },
      });
      return data;
    },
    enabled: !!periodo,
  });
}

export function useTurnoverAbsenteismo(periodo: string) {
  return useQuery({
    queryKey: ['turnover-absenteismo', periodo],
    queryFn: async () => {
      const { data } = await api.get<TurnoverAbsenteismoLinha[]>('/relatorios/turnover-absenteismo', {
        params: { periodo },
      });
      return data;
    },
    enabled: !!periodo,
  });
}

export function useDRE(periodoInicio: string, periodoFim: string) {
  return useQuery({
    queryKey: ['dre-relatorio', periodoInicio, periodoFim],
    queryFn: async () => {
      const { data } = await api.get<DRELinha[]>('/relatorios/dre', {
        params: { periodoInicio, periodoFim },
      });
      return data;
    },
    enabled: !!periodoInicio && !!periodoFim,
  });
}

export function useSaldoAtual() {
  return useQuery({
    queryKey: ['saldo-atual'],
    queryFn: async () => {
      const { data } = await api.get<number>('/relatorios/saldo-atual');
      return data;
    },
  });
}

/**
 * Download autenticado de CSV/PDF: usa o axios (com token + refresh) e força
 * o salvamento do arquivo no navegador.
 */
export async function baixarRelatorio(
  path: string,
  params: Record<string, string>,
  filename: string,
) {
  const { data } = await api.get(path, { params, responseType: 'blob' });
  const url = URL.createObjectURL(data instanceof Blob ? data : new Blob([String(data)]));
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
  URL.revokeObjectURL(url);
}
