import { useQuery } from '@tanstack/react-query';
import { HeartHandshake, Plus, Shield, X } from 'lucide-react';
import { useState } from 'react';
import CrudLayout from '../components/CrudLayout';
import { useAuth } from '../contexts/AuthContext';
import { api } from '../lib/api';

interface Beneficio {
  id: string;
  nome: string;
  descricao: string;
  tipo: string;
  tipoCobranca: string;
  valor: number;
  percentualDesconto: number;
  ativo: boolean;
  obrigatorio: boolean;
}

interface FuncionarioBeneficio {
  id: string;
  funcionarioNome: string;
  beneficioNome: string;
  dataInicio: string;
  dataFim?: string;
  valorFuncionario: number;
  valorEmpresa: number;
  ativo: boolean;
}

export default function Beneficios() {
  const { hasPermission } = useAuth();
  const [tab, setTab] = useState<'todos' | 'ativos' | 'obrigatorios'>('ativos');
  const [showNovoBeneficioModal, setShowNovoBeneficioModal] = useState(false);

  const { data: beneficios } = useQuery({
    queryKey: ['beneficios'],
    queryFn: async () => {
      const { data } = await api.get<Beneficio[]>('/api/beneficios');
      return data;
    },
    enabled: hasPermission('rh') || hasPermission('admin'),
  });

  const { data: funcionariosBeneficios } = useQuery({
    queryKey: ['funcionariosBeneficios'],
    queryFn: async () => {
      const { data } = await api.get<FuncionarioBeneficio[]>('/api/beneficios/funcionarios');
      return data;
    },
    enabled: hasPermission('rh') || hasPermission('admin'),
  });

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

  const items = tab === 'ativos'
    ? beneficios?.filter(b => b.ativo)
    : tab === 'obrigatorios'
      ? beneficios?.filter(b => b.ativo && b.obrigatorio)
      : beneficios;

  return (
    <CrudLayout title="Gestão de Benefícios">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
        <div className="flex gap-2">
          <button
            onClick={() => setTab('ativos')}
            className={`px-4 py-2 rounded-lg ${tab === 'ativos'
                ? 'bg-green-500 text-white'
                : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Ativos
          </button>
          <button
            onClick={() => setTab('obrigatorios')}
            className={`px-4 py-2 rounded-lg ${tab === 'obrigatorios'
                ? 'bg-red-500/30 text-red-400'
                : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Obrigatórios
          </button>
          <button
            onClick={() => setTab('todos')}
            className={`px-4 py-2 rounded-lg ${tab === 'todos'
                ? 'bg-sky-500 text-white'
                : 'bg-slate-800 text-slate-400 hover:bg-slate-700'
              }`}
          >
            Todos
          </button>
        </div>
        <button
          onClick={() => setShowNovoBeneficioModal(true)}
          className="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-green-500/20 transition-all"
        >
          <Plus className="w-4 h-4" /> Novo Benefício
        </button>
      </div>

      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-700 bg-slate-800/50">
          <h3 className="text-lg font-bold text-white mb-4">Benefícios Disponíveis</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {items?.map((beneficio) => (
              <div key={beneficio.id} className="bg-slate-700/30 rounded-xl p-4 border border-slate-600/30 hover:border-green-500/50 transition-all">
                <div className="flex justify-between items-start mb-2">
                  <h4 className="font-bold text-white">{beneficio.nome}</h4>
                  <span className={`px-2 py-1 rounded text-[10px] uppercase font-bold ${beneficio.obrigatorio
                      ? 'bg-red-500/20 text-red-400'
                      : 'bg-green-500/20 text-green-400'
                    }`}>
                    {beneficio.obrigatorio ? 'Obrigatório' : 'Opcional'}
                  </span>
                </div>
                <p className="text-xs text-slate-400 mb-2">{beneficio.descricao}</p>
                <div className="text-xs text-slate-400 space-y-1">
                  <div>Tipo: {beneficio.tipo}</div>
                  <div>Cobrança: {beneficio.tipoCobranca}</div>
                  {beneficio.valor > 0 && <div>Valor: R$ {beneficio.valor.toFixed(2)}</div>}
                  {beneficio.percentualDesconto > 0 && <div>Desconto: {beneficio.percentualDesconto}%</div>}
                </div>
              </div>
            ))}
            {items?.length === 0 && (
              <div className="col-span-full text-center py-8 text-slate-500">
                Nenhum benefício encontrado.
              </div>
            )}
          </div>
        </div>
      </div>

      <div className="mt-6 bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        <div className="p-6 border-b border-slate-700 bg-slate-800/50">
          <h3 className="text-lg font-bold text-white mb-4">
            Benefícios por Funcionário ({funcionariosBeneficios?.length || 0})
          </h3>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-900/80 border-b border-slate-700 text-slate-400 uppercase text-[10px] font-bold tracking-wider">
                <tr>
                  <th className="p-4">Funcionário</th>
                  <th className="p-4">Benefício</th>
                  <th className="p-4 text-center">Status</th>
                  <th className="p-4 text-right">Custo Funcionário</th>
                  <th className="p-4 text-right">Custo Empresa</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-700/50">
                {funcionariosBeneficios?.map((item) => (
                  <tr key={item.id} className="hover:bg-slate-700/20 transition-colors">
                    <td className="p-4">
                      <div className="font-medium text-white">{item.funcionarioNome}</div>
                      <div className="text-xs text-slate-500">
                        Início: {new Date(item.dataInicio).toLocaleDateString('pt-BR')}
                      </div>
                    </td>
                    <td className="p-4 text-slate-300">{item.beneficioNome}</td>
                    <td className="p-4 text-center">
                      <span className={`px-2 py-1 rounded text-[10px] font-bold ${item.ativo
                          ? 'bg-green-500/20 text-green-400'
                          : 'bg-slate-500/20 text-slate-400'
                        }`}>
                        {item.ativo ? 'Ativo' : 'Inativo'}
                      </span>
                    </td>
                    <td className="p-4 text-right text-slate-300">
                      R$ {item.valorFuncionario.toFixed(2)}
                    </td>
                    <td className="p-4 text-right text-green-400 font-bold">
                      R$ {item.valorEmpresa.toFixed(2)}
                    </td>
                  </tr>
                ))}
                {funcionariosBeneficios?.length === 0 && (
                  <tr>
                    <td colSpan={5} className="p-8 text-center text-slate-500">
                      Nenhum funcionário com benefício ativo.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {/* Modal Novo Benefício */}
      {showNovoBeneficioModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-lg overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <HeartHandshake className="w-5 h-5 text-green-400" /> Novo Benefício
              </h3>
              <button onClick={() => setShowNovoBeneficioModal(false)} className="text-slate-400 hover:text-white" type="button">
                <X className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={(e) => e.preventDefault()}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Nome do Benefício</label>
                <input required type="text" placeholder="Ex: Vale Transporte" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-green-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Tipo</label>
                <select className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-green-500">
                  <option value="VALE_TRANSPORTE">Vale Transporte</option>
                  <option value="VALE_REFEICAO">Vale Refeição</option>
                  <option value="VALE_ALIMENTACAO">Vale Alimentação</option>
                  <option value="PLANO_SAUDE">Plano de Saúde</option>
                  <option value="PLANO_ODONTOLOGICO">Plano Odontológico</option>
                  <option value="OUTROS">Outros</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Cobrança</label>
                <select className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-green-500">
                  <option value="VALOR_FIXO">Valor Fixo</option>
                  <option value="PERCENTUAL_SALARIO">Percentual sobre Salário</option>
                  <option value="EMPRESA_CUSTEIA_TOTAL">Empresa Custeia Total</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Valor / Percentual</label>
                <input type="number" step="0.01" min="0" placeholder="0.00" className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-green-500" />
              </div>
              <div className="flex items-center gap-2">
                <input type="checkbox" id="obrigatorio" className="rounded bg-slate-900 border-slate-700 text-green-500 focus:ring-green-500" />
                <label htmlFor="obrigatorio" className="text-sm font-medium text-slate-300">Obrigatório para todos os funcionários</label>
              </div>
              <button type="submit" className="mt-4 w-full py-2.5 rounded-lg font-bold text-white bg-green-500 hover:bg-green-600 transition-colors flex justify-center items-center gap-2">
                Criar Benefício
              </button>
            </form>
          </div>
        </div>
      )}
    </CrudLayout>
  );
}
