import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import {
  useEmployees,
  useAdmissoes,
  useCriarAdmissao,
  useAtualizarAdmissao,
  useConcluirAdmissao,
  useDemissoes,
  useCriarDemissao,
  useCalcularRescisao,
  useFinalizarDemissao,
} from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { ClipboardList, UserPlus, UserMinus, CheckCircle2, Calculator } from 'lucide-react';

// ── Helpers ─────────────────────────────────────────────

const fmtBrl = (v: number | string | null | undefined) =>
  v == null || v === ''
    ? '—'
    : Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

const CHECKLIST_KEYS = {
  rgEntregue: 'RG',
  cpfEntregue: 'CPF',
  ctpsEntregue: 'CTPS',
  comprovanteResidenciaEntregue: 'Comprovante de Residência',
  tituloEleitorEntregue: 'Título de Eleitor',
  certificadoReservista: 'Certificado de Reservista',
  exameMedicoRealizado: 'Exame Admissional',
  contratoAssinado: 'Contrato Assinado',
} as const;

const STATUS_ADMISSAO_LABELS: Record<string, string> = {
  PENDENTE: 'Pendente',
  EM_ANDAMENTO: 'Em Andamento',
  CONCLUIDA: 'Concluída',
  CANCELADA: 'Cancelada',
};

const TIPO_DEMISSAO_LABELS: Record<string, string> = {
  SEM_JUSTA_CAUSA: 'Sem Justa Causa',
  COM_JUSTA_CAUSA: 'Com Justa Causa',
  PEDIDO: 'Pedido do Funcionário',
  TERMINO_CONTRATO: 'Término de Contrato',
};

const STATUS_DEMISSAO_LABELS: Record<string, string> = {
  INICIADA: 'Iniciada',
  CALCULADA: 'Rescisão Calculada',
  FINALIZADA: 'Finalizada',
  CANCELADA: 'Cancelada',
};

// ── Componente ──────────────────────────────────────────

export function AdmissoesDemissoes() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();
  const [aba, setAba] = useState<'admissoes' | 'demissoes'>('admissoes');

  const { data: employees, isLoading: loadingEmp } = useEmployees();
  const podeGerir = hasPermission('rh') || hasPermission('admin');

  if (loadingEmp) return <PageLoading text="Carregando..." />;
  if (!podeGerir && !hasPermission('financeiro')) {
    return <PageLoading text="Acesso restrito." />;
  }

  const funcionarios = (employees ?? []).filter((f: any) => f.ativo !== false);

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-3">
        <ClipboardList className="w-8 h-8 text-indigo-400" />
        <div>
          <h1 className="text-2xl font-bold text-white">Admissão & Demissão</h1>
          <p className="text-sm text-slate-400">
            Checklist do processo admissional e fluxo de desligamento com cálculo de rescisão.
          </p>
        </div>
      </div>

      {/* Abas */}
      <div className="flex gap-2">
        <button
          onClick={() => setAba('admissoes')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            aba === 'admissoes' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
          }`}
        >
          <UserPlus className="w-4 h-4" /> Admissões
        </button>
        <button
          onClick={() => setAba('demissoes')}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg font-medium transition-colors ${
            aba === 'demissoes' ? 'bg-indigo-600 text-white' : 'bg-slate-800 text-slate-300 hover:bg-slate-700'
          }`}
        >
          <UserMinus className="w-4 h-4" /> Desligamentos
        </button>
      </div>

      {aba === 'admissoes' ? (
        <AdmissoesTab podeGerir={podeGerir} funcionarios={funcionarios} showToast={showToast} />
      ) : (
        <DemissoesTab
          podeGerir={podeGerir}
          podeFinanceiro={hasPermission('financeiro') || podeGerir}
          funcionarios={funcionarios}
          showToast={showToast}
        />
      )}
    </div>
  );
}

// ── Aba Admissões ───────────────────────────────────────

function AdmissoesTab({
  podeGerir,
  funcionarios,
  showToast,
}: {
  podeGerir: boolean;
  funcionarios: any[];
  showToast: (msg: string, type: 'success' | 'error' | 'info') => void;
}) {
  const { data: admissoes, isLoading } = useAdmissoes();
  const criarAdmissao = useCriarAdmissao();
  const atualizarAdmissao = useAtualizarAdmissao();
  const concluirAdmissao = useConcluirAdmissao();

  const [novoFuncionario, setNovoFuncionario] = useState('');
  const [novaData, setNovaData] = useState(new Date().toISOString().slice(0, 10));

  const handleCriar = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!novoFuncionario) return showToast('Selecione o funcionário.', 'error');
    try {
      await criarAdmissao.mutateAsync({ funcionarioId: novoFuncionario, dataAdmissao: novaData });
      showToast('Processo admissional iniciado!', 'success');
      setNovoFuncionario('');
    } catch {
      showToast('Erro ao iniciar admissão.', 'error');
    }
  };

  const toggleItem = async (adm: any, key: string) => {
    const payload = { ...adm, [key]: !(adm as any)[key] };
    delete payload.funcionario;
    try {
      await atualizarAdmissao.mutateAsync({ id: adm.id, ...payload });
    } catch {
      showToast('Erro ao atualizar checklist.', 'error');
    }
  };

  const handleConcluir = async (id: string) => {
    try {
      await concluirAdmissao.mutateAsync(id);
      showToast('Admissão concluída!', 'success');
    } catch {
      showToast('Não é possível concluir: checklist incompleto.', 'error');
    }
  };

  const statusColor = (s: string) => {
    switch (s) {
      case 'CONCLUIDA': return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
      case 'EM_ANDAMENTO': return 'bg-blue-500/20 text-blue-400 border border-blue-500/30';
      case 'CANCELADA': return 'bg-red-500/20 text-red-400 border border-red-500/30';
      default: return 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30';
    }
  };

  if (isLoading) return <PageLoading text="Carregando admissões..." />;

  return (
    <div className="space-y-6">
      {podeGerir && (
        <form onSubmit={handleCriar} className="bg-slate-800 border border-slate-700 rounded-xl p-5 flex flex-wrap items-end gap-4">
          <div className="min-w-[240px] flex-1">
            <label className="block text-sm font-medium text-slate-300 mb-1">Funcionário a admitir</label>
            <select
              value={novoFuncionario}
              onChange={(e) => setNovoFuncionario(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            >
              <option value="">Selecione...</option>
              {funcionarios.map((f: any) => (
                <option key={f.id} value={f.id}>{f.nome}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Data de Admissão</label>
            <input
              type="date"
              required
              value={novaData}
              onChange={(e) => setNovaData(e.target.value)}
              className="bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <button
            type="submit"
            disabled={criarAdmissao.isPending}
            className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg font-medium"
          >
            <UserPlus className="w-4 h-4" /> Iniciar Processo
          </button>
        </form>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {(admissoes ?? []).map((adm: any) => {
          const itens = Object.entries(CHECKLIST_KEYS) as [string, string][];
          const feitos = itens.filter(([k]) => !!(adm as any)[k]).length;
          return (
            <div key={adm.id} className="bg-slate-800 border border-slate-700 rounded-xl p-5 space-y-3">
              <div className="flex items-start justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-white">
                    {adm.funcionario?.nome ?? `Funcionário ${String(adm.funcionarioId).slice(0, 8)}`}
                  </h3>
                  <p className="text-sm text-slate-400">Admissão: {adm.dataAdmissao}</p>
                </div>
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColor(adm.status)}`}>
                  {STATUS_ADMISSAO_LABELS[adm.status] ?? adm.status}
                </span>
              </div>

              <div className="text-xs text-slate-400">
                Checklist: {feitos}/{itens.length} itens
                <div className="h-1.5 bg-slate-700 rounded-full mt-1">
                  <div
                    className="h-1.5 bg-indigo-500 rounded-full transition-all"
                    style={{ width: `${(feitos / itens.length) * 100}%` }}
                  />
                </div>
              </div>

              <ul className="grid grid-cols-2 gap-1.5">
                {itens.map(([key, label]) => (
                  <li key={key}>
                    <label className={`flex items-center gap-2 text-sm ${podeGerir && adm.status !== 'CONCLUIDA' ? 'cursor-pointer' : 'cursor-default'}`}>
                      <input
                        type="checkbox"
                        disabled={!podeGerir || adm.status === 'CONCLUIDA'}
                        checked={!!(adm as any)[key]}
                        onChange={() => toggleItem(adm, key)}
                        className="rounded bg-slate-900 border-slate-600 text-indigo-500"
                      />
                      <span className={!!(adm as any)[key] ? 'text-slate-300' : 'text-slate-500'}>{label}</span>
                    </label>
                  </li>
                ))}
              </ul>

              {podeGerir && adm.status !== 'CONCLUIDA' && adm.status !== 'CANCELADA' && (
                <button
                  onClick={() => handleConcluir(adm.id)}
                  disabled={concluirAdmissao.isPending}
                  className="flex items-center gap-2 text-sm bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white px-3 py-1.5 rounded-lg"
                >
                  <CheckCircle2 className="w-4 h-4" /> Concluir Admissão
                </button>
              )}
            </div>
          );
        })}
        {(admissoes ?? []).length === 0 && (
          <p className="text-slate-400">Nenhum processo admissional em andamento.</p>
        )}
      </div>
    </div>
  );
}

// ── Aba Demissões ───────────────────────────────────────

function DemissoesTab({
  podeGerir,
  podeFinanceiro,
  funcionarios,
  showToast,
}: {
  podeGerir: boolean;
  podeFinanceiro: boolean;
  funcionarios: any[];
  showToast: (msg: string, type: 'success' | 'error' | 'info') => void;
}) {
  const { data: demissoes, isLoading } = useDemissoes();
  const criarDemissao = useCriarDemissao();
  const calcularRescisao = useCalcularRescisao();
  const finalizarDemissao = useFinalizarDemissao();

  const [form, setForm] = useState({
    funcionarioId: '',
    dataDemissao: new Date().toISOString().slice(0, 10),
    tipoDemissao: 'SEM_JUSTA_CAUSA',
    avisoPrevioTrabalhado: true,
    avisoPrevioIndenizado: false,
    motivo: '',
  });

  const handleCriar = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.funcionarioId) return showToast('Selecione o funcionário.', 'error');
    try {
      await criarDemissao.mutateAsync(form);
      showToast('Desligamento registrado!', 'success');
      setForm({ ...form, funcionarioId: '', motivo: '' });
    } catch {
      showToast('Erro ao registrar desligamento.', 'error');
    }
  };

  const handleCalcular = async (id: string) => {
    try {
      const d = await calcularRescisao.mutateAsync(id);
      showToast(`Rescisão calculada: ${fmtBrl(d.valorTotalRescisao)}`, 'success');
    } catch {
      showToast('Erro ao calcular rescisão.', 'error');
    }
  };

  const handleFinalizar = async (id: string) => {
    if (!window.confirm('Confirmar encerramento definitivo do desligamento?')) return;
    try {
      await finalizarDemissao.mutateAsync(id);
      showToast('Desligamento finalizado!', 'success');
    } catch {
      showToast('Erro ao finalizar (calcule a rescisão antes).', 'error');
    }
  };

  const statusColor = (s: string) => {
    switch (s) {
      case 'FINALIZADA': return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
      case 'CALCULADA': return 'bg-blue-500/20 text-blue-400 border border-blue-500/30';
      case 'CANCELADA': return 'bg-red-500/20 text-red-400 border border-red-500/30';
      default: return 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30';
    }
  };

  if (isLoading) return <PageLoading text="Carregando desligamentos..." />;

  return (
    <div className="space-y-6">
      {podeGerir && (
        <form onSubmit={handleCriar} className="bg-slate-800 border border-slate-700 rounded-xl p-5 space-y-4">
          <h2 className="text-lg font-semibold text-white">Novo Desligamento</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Funcionário</label>
              <select
                value={form.funcionarioId}
                onChange={(e) => setForm({ ...form, funcionarioId: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
              >
                <option value="">Selecione...</option>
                {funcionarios.map((f: any) => (
                  <option key={f.id} value={f.id}>{f.nome}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Data do Desligamento</label>
              <input
                type="date"
                required
                value={form.dataDemissao}
                onChange={(e) => setForm({ ...form, dataDemissao: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Tipo</label>
              <select
                value={form.tipoDemissao}
                onChange={(e) => setForm({ ...form, tipoDemissao: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
              >
                {Object.entries(TIPO_DEMISSAO_LABELS).map(([k, v]) => (
                  <option key={k} value={k}>{v}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-300 mb-1">Motivo</label>
              <input
                type="text"
                value={form.motivo}
                onChange={(e) => setForm({ ...form, motivo: e.target.value })}
                className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
              />
            </div>
            <div className="flex items-end gap-4">
              <label className="flex items-center gap-2 text-sm text-slate-300">
                <input
                  type="checkbox"
                  checked={form.avisoPrevioTrabalhado}
                  onChange={(e) => setForm({ ...form, avisoPrevioTrabalhado: e.target.checked })}
                  className="rounded bg-slate-900 border-slate-600 text-indigo-500"
                />
                Aviso trabalhado
              </label>
              <label className="flex items-center gap-2 text-sm text-slate-300">
                <input
                  type="checkbox"
                  checked={form.avisoPrevioIndenizado}
                  onChange={(e) => setForm({ ...form, avisoPrevioIndenizado: e.target.checked })}
                  className="rounded bg-slate-900 border-slate-600 text-indigo-500"
                />
                Aviso indenizado
              </label>
            </div>
            <div className="flex items-end">
              <button
                type="submit"
                disabled={criarDemissao.isPending}
                className="flex items-center gap-2 bg-red-600 hover:bg-red-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg font-medium"
              >
                <UserMinus className="w-4 h-4" /> Registrar Desligamento
              </button>
            </div>
          </div>
        </form>
      )}

      <div className="space-y-4">
        {(demissoes ?? []).map((dem: any) => (
          <div key={dem.id} className="bg-slate-800 border border-slate-700 rounded-xl p-5 space-y-3">
            <div className="flex items-start justify-between flex-wrap gap-2">
              <div>
                <h3 className="text-lg font-semibold text-white">
                  {dem.funcionario?.nome ?? `Funcionário ${String(dem.funcionarioId).slice(0, 8)}`}
                </h3>
                <p className="text-sm text-slate-400">
                  {TIPO_DEMISSAO_LABELS[dem.tipoDemissao] ?? dem.tipoDemissao} · Data: {dem.dataDemissao}
                  {dem.motivo ? ` · ${dem.motivo}` : ''}
                </p>
              </div>
              <span className={`px-2 py-1 rounded-full text-xs font-medium ${statusColor(dem.status)}`}>
                {STATUS_DEMISSAO_LABELS[dem.status] ?? dem.status}
              </span>
            </div>

            {dem.status !== 'INICIADA' && (
              <div className="grid grid-cols-2 md:grid-cols-4 gap-x-6 gap-y-2 text-sm border-t border-slate-700 pt-3">
                <div><span className="text-slate-400">Saldo salário:</span> <span className="text-white">{fmtBrl(dem.saldoSalario)}</span></div>
                <div><span className="text-slate-400">Férias vencidas:</span> <span className="text-white">{fmtBrl(dem.feriasVencidas)}</span></div>
                <div><span className="text-slate-400">Férias proporc.:**</span> <span className="text-white">{fmtBrl(dem.feriasProporcionais)}</span></div>
                <div><span className="text-slate-400">13º proporcional:</span> <span className="text-white">{fmtBrl(dem.decimoTerceiroProporcional)}</span></div>
                <div><span className="text-slate-400">Aviso prévio:</span> <span className="text-white">{fmtBrl(dem.avisoPrevioIndenizadoValor)}</span></div>
                <div><span className="text-slate-400">FGTS:</span> <span className="text-white">{fmtBrl(dem.fgts)}</span></div>
                <div><span className="text-slate-400">Multa FGTS (40%):</span> <span className="text-white">{fmtBrl(dem.multaFgts)}</span></div>
                <div><span className="text-slate-400 font-medium">Total rescisão:</span> <span className="text-emerald-400 font-bold">{fmtBrl(dem.valorTotalRescisao)}</span></div>
              </div>
            )}

            <div className="flex gap-3 flex-wrap">
              {podeFinanceiro && dem.status === 'INICIADA' && (
                <button
                  onClick={() => handleCalcular(dem.id)}
                  disabled={calcularRescisao.isPending}
                  className="flex items-center gap-2 text-sm bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white px-3 py-1.5 rounded-lg"
                >
                  <Calculator className="w-4 h-4" /> Calcular Rescisão
                </button>
              )}
              {podeGerir && dem.status === 'CALCULADA' && (
                <button
                  onClick={() => handleFinalizar(dem.id)}
                  disabled={finalizarDemissao.isPending}
                  className="flex items-center gap-2 text-sm bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white px-3 py-1.5 rounded-lg"
                >
                  <CheckCircle2 className="w-4 h-4" /> Finalizar Desligamento
                </button>
              )}
            </div>
          </div>
        ))}
        {(demissoes ?? []).length === 0 && (
          <p className="text-slate-400">Nenhum desligamento registrado.</p>
        )}
      </div>
    </div>
  );
}
