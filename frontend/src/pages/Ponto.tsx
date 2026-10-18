import { useMemo, useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import {
  useEmployees,
  useRegistrosPonto,
  useCalcularHorasPontoDetalhado,
  useRegistrarEntrada,
  useRegistrarSaida,
  useSalvarPontoCompleto,
  useRegistrarPausaNR17,
  baixarAFD,
} from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { Clock, LogIn, LogOut, Timer, Save, Coffee, FileDown } from 'lucide-react';

const hoje = () => new Date().toISOString().slice(0, 10);
const inicioMes = () => `${new Date().toISOString().slice(0, 7)}-01`;
const fimMes = () => {
  const d = new Date();
  const ultimo = new Date(d.getFullYear(), d.getMonth() + 1, 0);
  return ultimo.toISOString().slice(0, 10);
};

const fmtHora = (t: string | null | undefined) => (t ? t.slice(0, 5) : '—');
const fmtHs = (v: number | undefined) => (v == null ? '—' : `${v.toFixed(2)}h`);

export function Ponto() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();

  const [funcionarioId, setFuncionarioId] = useState('');
  const [inicio, setInicio] = useState(inicioMes());
  const [fim, setFim] = useState(fimMes());

  // registro manual (espelho / ajuste do supervisor)
  const [form, setForm] = useState({
    data: hoje(),
    horaEntrada: '09:00',
    horaIntervaloInicio: '12:00',
    horaIntervaloFim: '13:00',
    horaSaida: '18:00',
    observacoes: '',
  });

  const { data: employees, isLoading: loadingEmp } = useEmployees();
  const { data: registros, isLoading } = useRegistrosPonto(funcionarioId || undefined, inicio, fim);
  const { data: resumo } = useCalcularHorasPontoDetalhado(funcionarioId || undefined, inicio, fim);
  const pausaNr17 = useRegistrarPausaNR17();

  const registrarEntrada = useRegistrarEntrada();
  const registrarSaida = useRegistrarSaida();
  const salvarCompleto = useSalvarPontoCompleto();

  const funcionarios = useMemo(
    () => (employees ?? []).filter((f: any) => f.ativo !== false),
    [employees],
  );

  if (loadingEmp) return <PageLoading text="Carregando funcionários..." />;

  if (!hasPermission('rh') && !hasPermission('admin') && !hasPermission('supervisor')) {
    return <PageLoading text="Acesso restrito." />;
  }

  const baterEntrada = async () => {
    if (!funcionarioId) return showToast('Selecione um funcionário.', 'error');
    try {
      await registrarEntrada.mutateAsync({ funcionarioId, data: hoje() });
      showToast('Entrada registrada!', 'success');
    } catch {
      showToast('Erro ao registrar entrada.', 'error');
    }
  };

  const baterSaida = async () => {
    if (!funcionarioId) return showToast('Selecione um funcionário.', 'error');
    try {
      await registrarSaida.mutateAsync({ funcionarioId, data: hoje() });
      showToast('Saída registrada!', 'success');
    } catch {
      showToast('Erro ao registrar saída.', 'error');
    }
  };

  const salvarAjuste = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!funcionarioId) return showToast('Selecione um funcionário.', 'error');
    try {
      await salvarCompleto.mutateAsync({ funcionarioId, ...form });
      showToast('Registro de ponto salvo!', 'success');
    } catch {
      showToast('Erro ao salvar registro.', 'error');
    }
  };

  const getStatusColor = (r: any) => {
    if (!r.horaSaida) return 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30';
    if (r.horaEntrada && r.horaSaida) return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30';
    return 'bg-slate-500/20 text-slate-400 border border-slate-500/30';
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-3">
        <Clock className="w-8 h-8 text-indigo-400" />
        <div>
          <h1 className="text-2xl font-bold text-white">Controle de Ponto Eletrônico</h1>
          <p className="text-sm text-slate-400">
            Registro de jornada, espelho de ponto mensal e ajustes pelo supervisor.
          </p>
        </div>
      </div>

      {/* Seleção e batida de ponto */}
      <div className="bg-slate-800 rounded-xl border border-slate-700 p-5 space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Funcionário</label>
            <select
              value={funcionarioId}
              onChange={(e) => setFuncionarioId(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            >
              <option value="">Selecione...</option>
              {funcionarios.map((f: any) => (
                <option key={f.id} value={f.id}>
                  {f.nome}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Início período</label>
            <input
              type="date"
              value={inicio}
              onChange={(e) => setInicio(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Fim período</label>
            <input
              type="date"
              value={fim}
              onChange={(e) => setFim(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>
        </div>

        <div className="flex flex-wrap gap-3">
          <button
            onClick={baterEntrada}
            disabled={registrarEntrada.isPending}
            className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg font-medium transition-colors"
          >
            <LogIn className="w-4 h-4" /> Bater Entrada
          </button>
          <button
            onClick={baterSaida}
            disabled={registrarSaida.isPending}
            className="flex items-center gap-2 bg-red-600 hover:bg-red-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg font-medium transition-colors"
          >
            <LogOut className="w-4 h-4" /> Bater Saída
          </button>
        </div>
      </div>

      {/* Totalizadores do período */}
      {funcionarioId && (
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
            <div className="flex items-center gap-2 text-slate-400 text-sm">
              <Timer className="w-4 h-4" /> Horas Normais
            </div>
            <p className="text-2xl font-bold text-white mt-1">{fmtHs(resumo?.horasNormais)}</p>
          </div>
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
            <div className="flex items-center gap-2 text-slate-400 text-sm">
              <Timer className="w-4 h-4" /> Extras 50%
            </div>
            <p className="text-2xl font-bold text-amber-400 mt-1">{fmtHs(resumo?.horasExtras50)}</p>
          </div>
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
            <div className="flex items-center gap-2 text-slate-400 text-sm">
              <Timer className="w-4 h-4" /> Extras 100%
            </div>
            <p className="text-2xl font-bold text-orange-400 mt-1">{fmtHs(resumo?.horasExtras100)}</p>
          </div>
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
            <div className="flex items-center gap-2 text-slate-400 text-sm">
              <Clock className="w-4 h-4" /> Total no Período
            </div>
            <p className="text-2xl font-bold text-indigo-400 mt-1">{fmtHs(resumo?.totalHoras)}</p>
          </div>
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-4 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-sm">
              <FileDown className="w-4 h-4" /> Portaria 671
            </div>
            <button
              onClick={() => baixarAFD(funcionarioId, inicio, fim)
                .then(() => showToast('AFD exportado com sucesso.', 'success'))
                .catch(() => showToast('Falha ao exportar AFD.', 'error'))}
              className="mt-2 flex items-center justify-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white px-3 py-2 rounded-lg text-sm font-medium transition-colors"
            >
              Exportar AFD
            </button>
          </div>
        </div>
      )}

      {/* Pausas NR-17 (teleatendimento) */}
      <div className="bg-slate-800 border border-slate-700 rounded-xl p-4">
        <div className="flex items-center gap-2 text-slate-300 font-semibold mb-1">
          <Coffee className="w-4 h-4 text-teal-400" /> Pausas NR-17 (Operador de Teleatendimento)
        </div>
        <p className="text-xs text-slate-500 mb-3">
          Pausa-descanso de 10 min a cada 50 min trabalhados e pausa-lanche de até 20 min diários.
        </p>
        <div className="flex flex-wrap gap-3">
          <button
            disabled={!funcionarioId || pausaNr17.isPending}
            onClick={() => pausaNr17.mutate(
              { funcionarioId, tipo: 'DESCANSO_10MIN' },
              { onSuccess: () => showToast('Pausa-descanso NR-17 registrada.', 'success'),
                onError: () => showToast('Falha ao registrar pausa.', 'error') }
            )}
            className="bg-teal-600 hover:bg-teal-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            Iniciar Pausa Descanso (10 min)
          </button>
          <button
            disabled={!funcionarioId || pausaNr17.isPending}
            onClick={() => pausaNr17.mutate(
              { funcionarioId, tipo: 'LANCHE_20MIN' },
              { onSuccess: () => showToast('Pausa-lanche NR-17 registrada.', 'success'),
                onError: () => showToast('Falha ao registrar pausa.', 'error') }
            )}
            className="bg-cyan-600 hover:bg-cyan-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-medium transition-colors"
          >
            Registrar Pausa Lanche (20 min)
          </button>
        </div>
      </div>

      {/* Espelho de ponto */}
      <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
        <div className="px-5 py-3 border-b border-slate-700">
          <h2 className="text-lg font-semibold text-white">Espelho de Ponto</h2>
        </div>
        {isLoading ? (
          <p className="p-5 text-slate-400">Carregando registros...</p>
        ) : !funcionarioId ? (
          <p className="p-5 text-slate-400">Selecione um funcionário para visualizar o espelho de ponto.</p>
        ) : !registros || registros.length === 0 ? (
          <p className="p-5 text-slate-400">Nenhum registro no período selecionado.</p>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-900 text-slate-400">
              <tr>
                <th className="text-left px-5 py-3">Data</th>
                <th className="text-left px-5 py-3">Entrada</th>
                <th className="text-left px-5 py-3">Intervalo</th>
                <th className="text-left px-5 py-3">Saída</th>
                <th className="text-left px-5 py-3">Situação</th>
                <th className="text-left px-5 py-3">Observações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700">
              {registros.map((r: any) => (
                <tr key={r.id} className="text-slate-300 hover:bg-slate-700/40">
                  <td className="px-5 py-3 text-white">{r.data}</td>
                  <td className="px-5 py-3">{fmtHora(r.horaEntrada)}</td>
                  <td className="px-5 py-3">
                    {fmtHora(r.horaIntervaloInicio)} – {fmtHora(r.horaIntervaloFim)}
                  </td>
                  <td className="px-5 py-3">{fmtHora(r.horaSaida)}</td>
                  <td className="px-5 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(r)}`}>
                      {r.horaSaida ? 'Completo' : r.horaEntrada ? 'Em jornada' : 'Incompleto'}
                    </span>
                  </td>
                  <td className="px-5 py-3 text-slate-400">{r.observacoes ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Ajuste manual (supervisor/RH) */}
      <div className="bg-slate-800 rounded-xl border border-slate-700 p-5">
        <h2 className="text-lg font-semibold text-white mb-4">Ajuste / Registro Manual (Supervisor)</h2>
        <form onSubmit={salvarAjuste} className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Data</label>
            <input
              type="date"
              required
              value={form.data}
              onChange={(e) => setForm({ ...form, data: e.target.value })}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Hora Entrada</label>
            <input
              type="time"
              required
              value={form.horaEntrada}
              onChange={(e) => setForm({ ...form, horaEntrada: e.target.value })}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Hora Saída</label>
            <input
              type="time"
              value={form.horaSaida}
              onChange={(e) => setForm({ ...form, horaSaida: e.target.value })}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Início Intervalo</label>
            <input
              type="time"
              value={form.horaIntervaloInicio}
              onChange={(e) => setForm({ ...form, horaIntervaloInicio: e.target.value })}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Fim Intervalo</label>
            <input
              type="time"
              value={form.horaIntervaloFim}
              onChange={(e) => setForm({ ...form, horaIntervaloFim: e.target.value })}
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-300 mb-1">Justificativa / Observação</label>
            <input
              type="text"
              value={form.observacoes}
              onChange={(e) => setForm({ ...form, observacoes: e.target.value })}
              placeholder="E.g. esquecimento de marcação"
              className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
            />
          </div>
          <div className="md:col-span-3">
            <button
              type="submit"
              disabled={salvarCompleto.isPending}
              className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white px-4 py-2 rounded-lg font-medium transition-colors"
            >
              <Save className="w-4 h-4" /> Salvar Ajuste
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
