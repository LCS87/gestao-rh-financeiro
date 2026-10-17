import { useQuery } from '@tanstack/react-query';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { Award, CalendarDays, Shield, XCircle } from 'lucide-react';
import { useState } from 'react';
import CrudLayout from '../components/CrudLayout';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../lib/api';

interface Avaliacao {
  id: string;
  funcionarioNome: string;
  cicloNome: string;
  tipo: string;
  status: 'PENDENTE' | 'EM_ANDAMENTO' | 'CONCLUIDA' | 'CANCELADA';
  percentualConclusao: number;
  notaFinal?: number;
  dataInicio?: string;
  dataConclusao?: string;
}

interface Ciclo {
  id: string;
  nome: string;
  tipo: string;
  status: string;
  dataInicio: string;
  dataFim: string;
  ativo: boolean;
}

export default function Avaliacoes() {
  const { hasPermission } = useAuth();
  const [tab, setTab] = useState<'todos' | 'pendentes' | 'concluidas'>('todos');
  const [showNovoCicloModal, setShowNovoCicloModal] = useState(false);

  const { data: ciclos } = useQuery({
    queryKey: ['ciclos'],
    queryFn: async () => {
      const { data } = await api.get<Ciclo[]>('/api/avaliacao/ciclos');
      return data;
    },
    enabled: hasPermission('rh') || hasPermission('admin'),
  });

  const { data: avaliacoes } = useQuery({
    queryKey: ['avaliacoes'],
    queryFn: async () => {
      const { data } = await api.get<Avaliacao[]>('/api/avaliacao/avaliacoes');
      return data;
    },
    enabled: hasPermission('rh') || hasPermission('admin'),
  });

  const statusColors = {
    'PENDENTE': 'text-yellow-400 bg-yellow-400/10 border-yellow-400/20',
    'EM_ANDAMENTO': 'text-blue-400 bg-blue-400/10 border-blue-400/20',
    'CONCLUIDA': 'text-green-400 bg-green-400/10 border-green-400/20',
    'CANCELADA': 'text-slate-400 bg-slate-400/10 border-slate-400/20',
  };

  const items = avaliacoes || [];

  if (!hasPermission('rh') && !hasPermission('admin')) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="text-center space-y-4">
          <Shield className="w-16 h-16 text-slate-600 mx-auto" />
          <h2 className="text-2xl font-bold text-white">Acesso Restrito</h2>
          <p className="text-slate-400">Esta área requer privilégios de RH ou Administrador.</p>
        </div>
      </div>
    );
  }

  return (
    <CrudLayout title="Avaliação de Desempenho">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
        <div className="flex gap-2">
          <button
            onClick={() => setTab('todos')}
            className={`px-4 py-2 rounded-lg ${tab === 'todos'
              ? 'bg-sky-500 text-white'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Todas
          </button>
          <button
            onClick={() => setTab('pendentes')}
            className={`px-4 py-2 rounded-lg ${tab === 'pendentes'
              ? 'bg-yellow-500/30 text-yellow-400'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Pendentes
          </button>
          <button
            onClick={() => setTab('concluidas')}
            className={`px-4 py-2 rounded-lg ${tab === 'concluidas'
              ? 'bg-green-500/30 text-green-400'
              : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Concluídas
          </button>
        </div>
        <button
          onClick={() => setShowNovoCicloModal(true)}
          className="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-blue-500/20 transition-all"
        >
          <Award className="w-4 h-4" /> Novo Ciclo
        </button>
      </div>

      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-700 bg-slate-800/50">
          <h3 className="text-lg font-bold text-white mb-4">Ciclos de Avaliação</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {ciclos?.map((ciclo) => (
              <div key={ciclo.id} className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/30 hover:border-blue-500/50 transition-all cursor-pointer">
                <div className="flex justify-between items-start mb-2">
                  <h4 className="font-bold text-white">{ciclo.nome}</h4>
                  <span className={`px-2 py-1 rounded text-[10px] uppercase font-bold ${ciclo.ativo ? 'bg-green-500/20 text-green-400' : 'bg-slate-500/20 text-slate-400'
                    }`}>
                    {ciclo.ativo ? 'Ativo' : 'Inativo'}
                  </span>
                </div>
                <div className="text-xs text-slate-400 mb-2">
                  <span className="flex items-center gap-1">
                    <CalendarDays className="w-3 h-3" />
                    {format(new Date(ciclo.dataInicio), 'dd/MM/yyyy', { locale: ptBR })} - {format(new Date(ciclo.dataFim), 'dd/MM/yyyy', { locale: ptBR })}
                  </span>
                </div>
                <div className="text-xs text-slate-400">
                  Tipo: {ciclo.tipo} | Status: {ciclo.status}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="mt-6 bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-700 bg-slate-800/50">
          <h3 className="text-lg font-bold text-white mb-4">Avaliações Realizadas</h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-900/80 border-b border-slate-700 text-slate-400 uppercase text-[10px] font-bold tracking-wider">
                <tr>
                  <th className="p-4">Funcionário</th>
                  <th className="p-4">Ciclo</th>
                  <th className="p-4">Tipo</th>
                  <th className="p-4 text-center">Status</th>
                  <th className="p-4 text-right">Progresso</th>
                  {tab === 'concluidas' && <th className="p-4 text-right">Nota</th>}
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {items.map((avaliacao) => (
                  <tr key={avaliacao.id} className="hover:bg-slate-700/20 transition-colors">
                    <td className="p-4">
                      <div className="font-medium text-white">{avaliacao.funcionarioNome}</div>
                      <div className="text-xs text-slate-500">{avaliacao.tipo}</div>
                    </td>
                    <td className="p-4 text-slate-300">{avaliacao.cicloNome}</td>
                    <td className="p-4 text-slate-400">{avaliacao.tipo}</td>
                    <td className="p-4 text-center">
                      <span className={`px-3 py-1 rounded text-xs font-bold uppercase border ${statusColors[avaliacao.status] || statusColors['PENDENTE']
                        }`}>
                        {avaliacao.status}
                      </span>
                    </td>
                    <td className="p-4 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <span className="text-sm font-bold text-slate-200">{avaliacao.percentualConclusao}%</span>
                        <div className="w-24 h-2 bg-slate-700 rounded-full overflow-hidden">
                          <div
                            className={`h-full rounded-full ${avaliacao.percentualConclusao >= 100 ? 'bg-green-500' : 'bg-blue-500'
                              }`}
                            style={{ width: `${avaliacao.percentualConclusao}%` }}
                          />
                        </div>
                      </div>
                    </td>
                    {tab === 'concluidas' && (
                      <td className="p-4 text-right">
                        <span className="text-lg font-black text-green-400">
                          {avaliacao.notaFinal?.toFixed(1)}
                        </span>
                      </td>
                    )}
                  </tr>
                ))}
                {items.length === 0 && (
                  <tr>
                    <td colSpan={6} className="p-8 text-center text-slate-500">
                      Nenhuma avaliação encontrada.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal Novo Ciclo */}
      {showNovoCicloModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-lg overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Award className="w-5 h-5 text-blue-400" /> Novo Ciclo de Avaliação
              </h3>
              <button onClick={() => setShowNovoCicloModal(false)} className="text-slate-400 hover:text-white" type="button">
                <XCircle className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={(e) => e.preventDefault()}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Nome do Ciclo</label>
                <input required type="text" placeholder="Ex: Avaliação Trimestral Q3/2026" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Tipo</label>
                  <select className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500">
                    <option value="TRIMESTRAL">Trimestral</option>
                    <option value="SEMESTRAL">Semestral</option>
                    <option value="ANUAL">Anual</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Ano</label>
                  <input required type="number" placeholder="2026" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Data Início</label>
                  <input required type="date" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Data Fim</label>
                  <input required type="date" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
                </div>
              </div>
              <button type="submit" className="mt-4 w-full py-2.5 rounded-lg font-bold text-white bg-blue-500 hover:bg-blue-600 transition-colors flex justify-center items-center gap-2">
                Criar Ciclo
              </button>
            </form>
          </div>
        </div>
      )}
    </CrudLayout>
  );
}
