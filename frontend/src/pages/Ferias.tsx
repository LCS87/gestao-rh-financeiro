import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useFerias, useCreateFerias, useUpdateFerias, useEmployees } from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Palmtree, Plus, Check, X as XIcon, Clock } from 'lucide-react';

export function Ferias() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();

  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({
    funcionarioId: '',
    dataInicio: '',
    dataFim: '',
    observacao: '',
    status: 'AGENDADA'
  });

  const { data: ferias, isLoading } = useFerias();
  const { data: employees } = useEmployees();
  const createFerias = useCreateFerias();
  const updateFerias = useUpdateFerias();

  if (!hasPermission('rh') && !hasPermission('admin')) {
    return <PageLoading text="Acesso restrito." />;
  }

  if (isLoading) return <PageLoading text="Carregando abonos e férias..." />;

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await createFerias.mutateAsync(formData);
      showToast('Férias lançadas!', 'success');
      setShowModal(false);
      setFormData({ funcionarioId: '', dataInicio: '', dataFim: '', observacao: '', status: 'AGENDADA' });
    } catch {
      showToast('Erro ao agendar férias.', 'error');
    }
  };

  const handleUpdateStatus = async (id: string, newStatus: string) => {
    try {
      const f = ferias?.find((x: any) => x.id === id);
      await updateFerias.mutateAsync({ id, ...f, status: newStatus });
      showToast('Status atualizado!', 'success');
    } catch {
      showToast('Erro ao atualizar.', 'error');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'AGENDADA': return 'bg-yellow-500/20 text-yellow-400 border border-yellow-500/30';
      case 'CONCLUIDA': return 'bg-slate-500/20 text-slate-400 border border-slate-500/30';
      case 'CANCELADA': return 'bg-red-500/20 text-red-400 border border-red-500/30';
      case 'EM_ANDAMENTO': return 'bg-blue-500/20 text-blue-400 border border-blue-500/30';
      default: return 'bg-blue-500/20 text-blue-400';
    }
  };

  return (
    <div className="max-w-[1600px] mx-auto space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white flex items-center gap-3">
            <Palmtree className="w-8 h-8 text-orange-400 bg-orange-400/10 rounded-lg p-1" />
            Gestão de Férias e Abonos
          </h1>
          <p className="text-slate-400 mt-1">Controle de descanso remunerado e afastamentos agendados.</p>
        </div>
        <button 
          onClick={() => setShowModal(true)}
          className="px-4 py-2 bg-gradient-to-r from-orange-500 to-amber-600 hover:from-orange-600 hover:to-amber-700 text-white rounded-xl font-medium flex items-center gap-2 shadow-lg shadow-orange-500/20 transition-all border border-orange-500/50"
        >
          <Plus className="w-4 h-4" /> Agendar Férias
        </button>
      </div>

      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-900/80 border-b border-slate-700 text-slate-400 uppercase text-[10px] font-bold tracking-wider">
              <tr>
                <th className="p-4">Funcionário</th>
                <th className="p-4">Período</th>
                <th className="p-4">Observação</th>
                <th className="p-4 text-center">Status</th>
                <th className="p-4 text-right">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700/50">
              {ferias?.map((f: any) => (
                <tr key={f.id} className="hover:bg-slate-700/20 transition-colors">
                  <td className="p-4 font-medium text-white">{f.funcionario?.nome || 'N/A'}</td>
                  <td className="p-4">
                    <div className="flex items-center gap-2 text-slate-300">
                      <Clock className="w-4 h-4 text-slate-500" />
                      {f.dataInicio ? format(new Date(f.dataInicio), 'dd/MM/yyyy', { locale: ptBR }) : '-'} 
                      {' até '}
                      {f.dataFim ? format(new Date(f.dataFim), 'dd/MM/yyyy', { locale: ptBR }) : '-'}
                    </div>
                  </td>
                  <td className="p-4 text-slate-400 text-xs">{f.observacao || '-'}</td>
                  <td className="p-4 text-center">
                    <span className={`px-2.5 py-1 rounded text-[10px] uppercase font-black tracking-wider ${getStatusColor(f.status)}`}>
                      {f.status}
                    </span>
                  </td>
                  <td className="p-4 text-right">
                    {f.status === 'AGENDADA' && (
                      <div className="flex items-center justify-end gap-2">
                        <button onClick={() => handleUpdateStatus(f.id, 'EM_ANDAMENTO')} title="Iniciar Férias" className="p-1.5 text-blue-400 bg-blue-400/10 hover:bg-blue-400/20 rounded-lg transition-colors">
                          <Check className="w-4 h-4" />
                        </button>
                        <button onClick={() => handleUpdateStatus(f.id, 'CANCELADA')} title="Cancelar Agendamento" className="p-1.5 text-red-400 bg-red-400/10 hover:bg-red-400/20 rounded-lg transition-colors">
                          <XIcon className="w-4 h-4" />
                        </button>
                      </div>
                    )}
                    {f.status === 'EM_ANDAMENTO' && (
                      <button onClick={() => handleUpdateStatus(f.id, 'CONCLUIDA')} title="Marcar como Concluída" className="p-1.5 text-slate-400 bg-slate-700 hover:text-white rounded-lg transition-colors">
                        <Check className="w-4 h-4" /> Finalizar
                      </button>
                    )}
                  </td>
                </tr>
              ))}
              {ferias?.length === 0 && (
                <tr>
                  <td colSpan={5} className="p-8 text-center text-slate-500">Nenhum registro de férias encontrado.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-md overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                 <Palmtree className="w-5 h-5 text-orange-400" /> Agendar Férias
              </h3>
              <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-white transition-colors">
                <XIcon className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={handleCreate}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Funcionário</label>
                <select required value={formData.funcionarioId} onChange={e => setFormData({...formData, funcionarioId: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                  <option value="" disabled>Selecione um colaborador</option>
                  {employees?.map(emp => (
                    <option key={emp.id} value={emp.id}>{emp.nome}</option>
                  ))}
                </select>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Data Início</label>
                  <input required type="date" value={formData.dataInicio} onChange={e => setFormData({...formData, dataInicio: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Data Fim</label>
                  <input required type="date" value={formData.dataFim} onChange={e => setFormData({...formData, dataFim: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Observações / Motivo</label>
                <input type="text" value={formData.observacao} onChange={e => setFormData({...formData, observacao: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" placeholder="Férias regulamentares..." />
              </div>
              <button type="submit" disabled={createFerias.isPending} className="mt-4 w-full py-2.5 rounded-lg font-bold text-white bg-gradient-to-r from-orange-500 to-amber-600 hover:from-orange-600 hover:to-amber-700 transition-colors flex justify-center items-center gap-2">
                Salvar Agendamento
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
