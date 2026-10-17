import {
  useQuery,
  useMutation,
  useQueryClient,
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../lib/api';
import type { 
  Funcionario, 
  FuncionarioForm, 
  Vendedor, 
  SalePayload, 
  DashboardIndicators,
  ContaPagar,
  ContaReceber,
  FolhaPagamento
} from '../types';

// ── React Query client ──────────────────────────────────

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000,
      retry: 1,
    },
  },
});

export function QueryProvider({ children }: { children: React.ReactNode }) {
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
}

// ── Employees (Funcionários) ────────────────────────────

export function useEmployees() {
  const { hasPermission } = useAuth();

  return useQuery<Funcionario[]>({
    queryKey: ['employees'],
    queryFn: async () => {
      const { data } = await api.get<Funcionario[]>('/rh/funcionarios');
      return data;
    },
    enabled: hasPermission('admin'),
  });
}

export function useCreateEmployee() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: FuncionarioForm) => {
      const { data } = await api.post<Funcionario>('/rh/funcionarios', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] }),
  });
}

export function useUpdateEmployee() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...body }: Partial<Funcionario> & { id: string }) => {
      const { data } = await api.put<Funcionario>(`/rh/funcionarios/${id}`, body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] }),
  });
}

export function useDeleteEmployee() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/rh/funcionarios/${id}`);
      return id;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['employees'] }),
  });
}

// ── Sales (Vendas) ──────────────────────────────────────

export function useSalesData() {
  const { user } = useAuth();

  return useQuery<Vendedor[]>({
    queryKey: ['sales', user?.level],
    queryFn: async () => {
      const { data } = await api.get<any[]>('/rh/vendas/ranking-diario');
      return data.map(v => ({
        id: v.vendedorId,
        nome: v.vendedorNome,
        metaDiaria: v.metaDiaria,
        vendasDiarias: v.vendasDiarias,
        vendasSemanais: v.vendasSemanais,
        vendasMensais: v.vendasMensais,
        status: 'ATIVO'
      }));
    },
  });
}

export function useResumoVendas() {
  return useQuery({
    queryKey: ['resumo-vendas'],
    queryFn: async () => {
      const { data } = await api.get('/rh/vendas/resumo-mensal');
      return data;
    },
  });
}

export function useHistoricoGlobal(dias: number = 30) {
  const { user } = useAuth();
  return useQuery({
    queryKey: ['historico-global', dias],
    queryFn: async () => {
      const { data } = await api.get(`/rh/vendas/historico-global?dias=${dias}`);
      return data;
    },
    enabled: !!user && (user.level === 'admin' || user.level === 'supervisor' || user.level === 'ti'),
  });
}

export function useHistoricoPessoal(vendedorId: string | undefined, dias: number = 30) {
  return useQuery({
    queryKey: ['historico-pessoal', vendedorId, dias],
    queryFn: async () => {
      const { data } = await api.get(`/rh/vendas/historico-pessoal/${vendedorId}?dias=${dias}`);
      return data;
    },
    enabled: !!vendedorId,
  });
}

export function useAddSale() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: SalePayload) => {
      const { data } = await api.post('/rh/vendas', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['sales'] }),
  });
}

// ── Dashboard ───────────────────────────────────────────

export function useDashboardIndicators() {
  return useQuery<DashboardIndicators>({
    queryKey: ['dashboard-indicators'],
    queryFn: async () => {
      const { data } = await api.get<DashboardIndicators>('/rh/dashboard/indicadores');
      return data;
    },
  });
}

// ── Financeiro ──────────────────────────────────────────

export function useContasPagar() {
  const { hasPermission } = useAuth();
  return useQuery<ContaPagar[]>({
    queryKey: ['contasPagar'],
    queryFn: async () => {
      const { data } = await api.get<ContaPagar[]>('/financeiro/contas-pagar');
      return data;
    },
    enabled: hasPermission('financeiro'),
  });
}

export function useContasReceber() {
  const { hasPermission } = useAuth();
  return useQuery<ContaReceber[]>({
    queryKey: ['contasReceber'],
    queryFn: async () => {
      const { data } = await api.get<ContaReceber[]>('/financeiro/contas-receber');
      return data;
    },
    enabled: hasPermission('financeiro'),
  });
}

export function useFolhaPagamento(anoMes?: string) {
  const { hasPermission } = useAuth();
  return useQuery<FolhaPagamento[]>({
    queryKey: ['folhaPagamento', anoMes],
    queryFn: async () => {
      const rota = anoMes ? `/financeiro/folha-pagamento/mes/${anoMes}` : '/financeiro/folha-pagamento';
      const { data } = await api.get<FolhaPagamento[]>(rota);
      return data;
    },
    enabled: hasPermission('financeiro') || hasPermission('rh'),
  });
}

export function useCreateContaPagar() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: Partial<ContaPagar>) => {
      const { data } = await api.post('/financeiro/contas-pagar', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['contasPagar'] }),
  });
}

export function useCreateContaReceber() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: Partial<ContaReceber>) => {
      const { data } = await api.post('/financeiro/contas-receber', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['contasReceber'] }),
  });
}

export function useGerarFolhaLote() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (anoMes: string) => {
      await api.post(`/financeiro/folha-pagamento/gerar-lote/${anoMes}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['folhaPagamento'] }),
  });
}

export function useUploadDocumento() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (formData: FormData) => {
      const { data } = await api.post('/rh/documentos/upload', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['documentos'] }),
  });
}

export function useFerias() {
  const { hasPermission } = useAuth();
  return useQuery<any[]>({
    queryKey: ['ferias'],
    queryFn: async () => {
      const { data } = await api.get('/rh/ferias');
      return data;
    },
    enabled: hasPermission('rh') || hasPermission('admin'),
  });
}

export function useCreateFerias() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: any) => {
      const { data } = await api.post('/rh/ferias', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ferias'] }),
  });
}

export function useUpdateFerias() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...body }: { id: string } & any) => {
      const { data } = await api.put(`/rh/ferias/${id}`, body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ferias'] }),
  });
}

// ── Ponto Eletrônico ────────────────────────────────────

export function useRegistrosPonto(funcionarioId: string | undefined, inicio: string, fim: string) {
  return useQuery<any[]>({
    queryKey: ['ponto', funcionarioId, inicio, fim],
    queryFn: async () => {
      const { data } = await api.get(`/rh/ponto/funcionario/${funcionarioId}?inicio=${inicio}&fim=${fim}`);
      return data;
    },
    enabled: !!funcionarioId,
  });
}

export function useCalcularHorasPonto(funcionarioId: string | undefined, inicio: string, fim: string) {
  return useQuery<{ horasNormais: number; horasExtras: number; totalHoras: number }>({
    queryKey: ['pontoHoras', funcionarioId, inicio, fim],
    queryFn: async () => {
      const { data } = await api.get(`/rh/ponto/funcionario/${funcionarioId}/calcular-horas?inicio=${inicio}&fim=${fim}`);
      return data;
    },
    enabled: !!funcionarioId,
  });
}

export function useRegistrarEntrada() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ funcionarioId, data }: { funcionarioId: string; data: string }) => {
      const res = await api.post(`/rh/ponto/entrada?funcionarioId=${funcionarioId}&data=${data}`);
      return res.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ponto'] }),
  });
}

export function useRegistrarSaida() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ funcionarioId, data }: { funcionarioId: string; data: string }) => {
      const res = await api.post(`/rh/ponto/saida?funcionarioId=${funcionarioId}&data=${data}`);
      return res.data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ponto'] }),
  });
}

export function useSalvarPontoCompleto() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: any) => {
      const { data } = await api.post('/rh/ponto', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['ponto'] }),
  });
}

// ── Escalas ─────────────────────────────────────────────

export function useEscalas() {
  return useQuery<any[]>({
    queryKey: ['escalas'],
    queryFn: async () => {
      const { data } = await api.get('/rh/escalas');
      return data;
    },
  });
}

export function useCriarEscala() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: any) => {
      const { data } = await api.post('/rh/escalas', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['escalas'] }),
  });
}

export function useAtualizarEscala() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...body }: { id: string } & any) => {
      const { data } = await api.put(`/rh/escalas/${id}`, body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['escalas'] }),
  });
}

export function useDeletarEscala() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      await api.delete(`/rh/escalas/${id}`);
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['escalas'] }),
  });
}

// ── Admissões ───────────────────────────────────────────

export function useAdmissoes() {
  return useQuery<any[]>({
    queryKey: ['admissoes'],
    queryFn: async () => {
      const { data } = await api.get('/rh/admissoes');
      return data;
    },
  });
}

export function useCriarAdmissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: any) => {
      const { data } = await api.post('/rh/admissoes', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admissoes'] }),
  });
}

export function useAtualizarAdmissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...body }: { id: string } & any) => {
      const { data } = await api.put(`/rh/admissoes/${id}`, body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admissoes'] }),
  });
}

export function useConcluirAdmissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post(`/rh/admissoes/${id}/concluir`);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['admissoes'] }),
  });
}

// ── Demissões ───────────────────────────────────────────

export function useDemissoes() {
  return useQuery<any[]>({
    queryKey: ['demissoes'],
    queryFn: async () => {
      const { data } = await api.get('/rh/demissoes');
      return data;
    },
  });
}

export function useCriarDemissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (body: any) => {
      const { data } = await api.post('/rh/demissoes', body);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['demissoes'] }),
  });
}

export function useCalcularRescisao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post(`/rh/demissoes/${id}/calcular-rescisao`);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['demissoes'] }),
  });
}

export function useFinalizarDemissao() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (id: string) => {
      const { data } = await api.post(`/rh/demissoes/${id}/finalizar`);
      return data;
    },
    onSuccess: () => qc.invalidateQueries({ queryKey: ['demissoes'] }),
  });
}
