import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import {
  useEscalas,
  useCriarEscala,
  useAtualizarEscala,
  useDeletarEscala,
} from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { CalendarDays, Plus, Pencil, Trash2, Users } from 'lucide-react';

const TIPO_LABELS: Record<string, string> = {
  COMERCIAL: 'Comercial (Seg–Sex, 8h)',
  C6X1: '6x1 (6 dias trabalhados, 1 folga)',
  C12X36: '12x36 (12h trabalho, 36h folga)',
  PLANTAO: 'Plantão',
  OUTRO: 'Outro',
};

const ESCALA_WORK_DAYS: Record<string, number[]> = {
  COMERCIAL: [1, 2, 3, 4, 5],           // Seg–Sex
  C6X1: [0, 1, 2, 3, 4, 5],            // Dom–Sex (folga sáb) — rotação simplificada
  C12X36: [0, 1, 2, 3, 4, 5, 6],       // cobertura diária (turnos alternados)
  PLANTAO: [0, 1, 2, 3, 4, 5, 6],
  OUTRO: [1, 2, 3, 4, 5],
};

const DIAS_SEMANA = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];

interface EscalaForm {
  nome: string;
  tipo: string;
  descricao: string;
  horasSemanais: number;
}

const FORM_VAZIO: EscalaForm = { nome: '', tipo: 'COMERCIAL', descricao: '', horasSemanais: 44 };

export function Escalas() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();

  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [form, setForm] = useState<EscalaForm>(FORM_VAZIO);
  const [escalaSelecionada, setEscalaSelecionada] = useState<string>('');

  const { data: escalas, isLoading } = useEscalas();
  const criarEscala = useCriarEscala();
  const atualizarEscala = useAtualizarEscala();
  const deletarEscala = useDeletarEscala();

  const podeGerir = hasPermission('rh') || hasPermission('admin');

  if (isLoading) return <PageLoading text="Carregando escalas..." />;

  if (!podeGerir && !hasPermission('supervisor')) {
    return <PageLoading text="Acesso restrito." />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const body = { ...form, horasSemanais: Number(form.horasSemanais) };
      if (editingId) {
        await atualizarEscala.mutateAsync({ id: editingId, ...body });
        showToast('Escala atualizada!', 'success');
      } else {
        await criarEscala.mutateAsync(body);
        showToast('Escala criada!', 'success');
      }
      setShowModal(false);
      setForm(FORM_VAZIO);
      setEditingId(null);
    } catch {
      showToast('Erro ao salvar escala.', 'error');
    }
  };

  const handleEdit = (esc: any) => {
    setEditingId(esc.id);
    setForm({
      nome: esc.nome,
      tipo: esc.tipo,
      descricao: esc.descricao ?? '',
      horasSemanais: esc.horasSemanais ?? 44,
    });
    setShowModal(true);
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Deseja realmente excluir esta escala?')) return;
    try {
      await deletarEscala.mutateAsync(id);
      showToast('Escala excluída!', 'success');
    } catch {
      showToast('Erro ao excluir (requer perfil TI).', 'error');
    }
  };

  const diasAtivos = escalaSelecionada
    ? ESCALA_WORK_DAYS[(escalas?.find((e: any) => e.id === escalaSelecionada)?.tipo) ?? ''] ?? []
    : [];

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <CalendarDays className="w-8 h-8 text-indigo-400" />
          <div>
            <h1 className="text-2xl font-bold text-white">Gestão de Escalas</h1>
            <p className="text-sm text-slate-400">Escalas para operação de call center (6x1, 12x36, comercial).</p>
          </div>
        </div>
        {podeGerir && (
          <button
            onClick={() => {
              setEditingId(null);
              setForm(FORM_VAZIO);
              setShowModal(true);
            }}
            className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white px-4 py-2 rounded-lg font-medium transition-colors"
          >
            <Plus className="w-4 h-4" /> Nova Escala
          </button>
        )}
      </div>

      {/* Lista de escalas */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {(escalas ?? []).map((esc: any) => (
          <div
            key={esc.id}
            className={`bg-slate-800 border rounded-xl p-5 cursor-pointer transition-colors ${
              escalaSelecionada === esc.id ? 'border-indigo-500' : 'border-slate-700 hover:border-slate-500'
            }`}
            onClick={() => setEscalaSelecionada(escalaSelecionada === esc.id ? '' : esc.id)}
          >
            <div className="flex items-start justify-between">
              <h3 className="text-lg font-semibold text-white">{esc.nome}</h3>
              {podeGerir && (
                <div className="flex gap-1">
                  <button
                    onClick={(e) => { e.stopPropagation(); handleEdit(esc); }}
                    className="p-1.5 text-slate-400 hover:text-indigo-400"
                    title="Editar"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  {hasPermission('ti') && (
                    <button
                      onClick={(e) => { e.stopPropagation(); handleDelete(esc.id); }}
                      className="p-1.5 text-slate-400 hover:text-red-400"
                      title="Excluir"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </div>
              )}
            </div>
            <span className="inline-block mt-2 px-2 py-1 rounded-full text-xs font-medium bg-indigo-500/20 text-indigo-300">
              {TIPO_LABELS[esc.tipo] ?? esc.tipo}
            </span>
            <p className="text-sm text-slate-400 mt-2">{esc.descricao ?? 'Sem descrição'}</p>
            <p className="text-sm text-slate-300 mt-2 font-medium">{esc.horasSemanais ?? 44}h semanais</p>
          </div>
        ))}
        {(escalas ?? []).length === 0 && (
          <p className="text-slate-400 col-span-full">Nenhuma escala cadastrada. Clique em "Nova Escala".</p>
        )}
      </div>

      {/* Calendário semanal da escala selecionada */}
      {escalaSelecionada && (
        <div className="bg-slate-800 rounded-xl border border-slate-700 p-5">
          <h2 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
            <Users className="w-5 h-5 text-indigo-400" />
            Grade Semanal — {escalas?.find((e: any) => e.id === escalaSelecionada)?.nome}
          </h2>
          <div className="grid grid-cols-7 gap-2">
            {DIAS_SEMANA.map((dia, idx) => (
              <div
                key={dia}
                className={`rounded-lg p-3 text-center text-sm font-medium ${
                  diasAtivos.includes(idx)
                    ? 'bg-emerald-500/20 text-emerald-300 border border-emerald-500/30'
                    : 'bg-slate-700/50 text-slate-500 border border-slate-600'
                }`}
              >
                <div>{dia}</div>
                <div className="text-xs mt-1">{diasAtivos.includes(idx) ? 'Trabalho' : 'Folga'}</div>
              </div>
            ))}
          </div>
          <p className="text-xs text-slate-500 mt-3">
            * A vinculação individual de operadores à escala é feita no cadastro do funcionário (campo escala).
          </p>
        </div>
      )}

      {/* Modal criar/editar */}
      {showModal && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4">
          <div className="bg-slate-800 border border-slate-700 rounded-xl p-6 w-full max-w-md">
            <h2 className="text-xl font-bold text-white mb-4">
              {editingId ? 'Editar Escala' : 'Nova Escala'}
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Nome</label>
                <input
                  type="text"
                  required
                  value={form.nome}
                  onChange={(e) => setForm({ ...form, nome: e.target.value })}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
                  placeholder="E.g. Tarde 12x36 - Tier 1"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Tipo</label>
                <select
                  value={form.tipo}
                  onChange={(e) => setForm({ ...form, tipo: e.target.value })}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
                >
                  {Object.entries(TIPO_LABELS).map(([k, v]) => (
                    <option key={k} value={k}>{v}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Horas Semanais</label>
                <input
                  type="number"
                  min="0"
                  step="0.5"
                  value={form.horasSemanais}
                  onChange={(e) => setForm({ ...form, horasSemanais: Number(e.target.value) })}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Descrição</label>
                <textarea
                  value={form.descricao}
                  onChange={(e) => setForm({ ...form, descricao: e.target.value })}
                  rows={3}
                  className="w-full bg-slate-900 border border-slate-700 rounded-lg px-3 py-2 text-white"
                />
              </div>
              <div className="flex gap-3 justify-end pt-2">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-lg text-slate-300 hover:bg-slate-700"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={criarEscala.isPending || atualizarEscala.isPending}
                  className="px-4 py-2 rounded-lg bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white font-medium"
                >
                  {editingId ? 'Salvar Alterações' : 'Criar Escala'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
