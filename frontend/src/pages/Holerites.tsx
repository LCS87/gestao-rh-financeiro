import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useEmployees, useFolhaPagamento, useGerarFolhaLote } from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { api } from '../lib/api';
import { Shield, FileText, CalendarDays, Calculator, Printer, Settings2, HandCoins, AlertCircle, CheckCircle2 } from 'lucide-react';
import type { FolhaPagamento, Funcionario } from '../types';

export function Holerites() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();

  const [selectedMes, setSelectedMes] = useState(
    `${new Date().getFullYear()}-${String(new Date().getMonth() + 1).padStart(2, '0')}`
  );
  const [selectedFuncionario, setSelectedFuncionario] = useState<Funcionario | null>(null);
  const [folhaAtual, setFolhaAtual] = useState<FolhaPagamento | null>(null);
  const [isProcessing, setIsProcessing] = useState(false);

  const { data: employees, isLoading: loadingEmp } = useEmployees();
  const { data: folhas, isLoading: loadingFolha, refetch: refetchFolhas } = useFolhaPagamento(selectedMes);
  const gerarFolhaLote = useGerarFolhaLote();

  if (!hasPermission('financeiro') && !hasPermission('rh')) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="text-center space-y-4">
          <Shield className="w-16 h-16 text-slate-600 mx-auto" />
          <h2 className="text-2xl font-bold text-white">Acesso Restrito</h2>
          <p className="text-slate-400">Esta área requer privilégios de RH ou Financeiro.</p>
        </div>
      </div>
    );
  }

  if (loadingEmp || loadingFolha) {
    return <PageLoading text="Carregando dados da folha..." />;
  }

  const handleSelectFuncionario = async (func: Funcionario) => {
    setSelectedFuncionario(func);
    const f = folhas?.find(f => f.funcionarioOrigemId === func.id) || null;
    setFolhaAtual(f);
  };

  const handleProcessarHolerite = async () => {
    if (!selectedFuncionario) return;
    setIsProcessing(true);
    
    try {
      let createdOrFoundId = folhaAtual?.id;

      // 1. Cria a folha se ela não existir
      if (!createdOrFoundId) {
        const { data: novaFolha } = await api.post(`/financeiro/folha-pagamento/funcionario/${selectedFuncionario.id}/mes/${selectedMes}`);
        createdOrFoundId = novaFolha.id;
      }

      // 2. Processa os cálculos padrões (INSS, IRRF se for CLT, pró-labore etc)
      const { data: folhaProcessada } = await api.post(`/financeiro/folha-pagamento/${createdOrFoundId}/processar?numeroDependentes=0`);
      
      setFolhaAtual(folhaProcessada);
      refetchFolhas();
      showToast('Holerite processado com sucesso!', 'success');
    } catch (err: any) {
      showToast('Erro ao processar folha de pagamento.', 'error');
    } finally {
      setIsProcessing(false);
    }
  };

  const statusColors = {
    'PROCESSANDO': 'text-yellow-400 bg-yellow-400/10 border-yellow-400/20',
    'FECHADA': 'text-blue-400 bg-blue-400/10 border-blue-400/20',
    'PAGAMENTO_AGENDADO': 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20',
    'PAGA': 'text-green-400 bg-green-400/10 border-green-400/20',
    'CANCELADA': 'text-slate-400 bg-slate-400/10 border-slate-400/20',
  };

  const handleGerarEmLote = async () => {
    try {
      if (confirm(`Gerar folhas de pagamento para todos os funcionários ativos na competência ${selectedMes}?`)) {
        await gerarFolhaLote.mutateAsync(selectedMes);
        showToast('Lote gerado com sucesso!', 'success');
        refetchFolhas();
      }
    } catch (e) {
      showToast('Erro ao gerar lote.', 'error');
    }
  };

  return (
    <div className="max-w-[1600px] mx-auto space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white flex items-center gap-3">
            <Calculator className="w-8 h-8 text-blue-400 bg-blue-400/10 rounded-lg p-1" />
            Folha de Pagamento & Holerites
          </h1>
          <p className="text-slate-400 mt-1">Geração de demonstrativos, pró-labore e provisões.</p>
        </div>
        
        <div className="flex items-center gap-3">
          <CalendarDays className="w-5 h-5 text-slate-400" />
          <input
            type="month"
            value={selectedMes}
            onChange={(e) => {
              setSelectedMes(e.target.value);
              setSelectedFuncionario(null);
            }}
            className="bg-slate-800 border border-slate-700 rounded-lg px-4 py-2 text-white outline-none focus:border-blue-500 transition-all font-mono shadow-inner"
          />
          <button 
             onClick={handleGerarEmLote}
             disabled={gerarFolhaLote.isPending}
             className="px-4 py-2 bg-gradient-to-r from-emerald-500 to-green-600 hover:from-emerald-600 hover:to-green-700 disabled:opacity-50 text-white rounded-xl font-medium flex items-center gap-2 transition-all shadow-lg shadow-green-500/20"
           >
             {gerarFolhaLote.isPending ? 'Gerando...' : 'Gerar Folha da Equipe'}
           </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Painel Esquerdo: Lista de Funcionários */}
        <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-xl overflow-hidden flex flex-col h-[700px]">
          <div className="p-4 border-b border-slate-700 bg-slate-800/80">
            <h3 className="font-bold text-white uppercase text-xs tracking-wider flex items-center gap-2">
              <Settings2 className="w-4 h-4 text-slate-400" /> Funcionários
            </h3>
          </div>
          <div className="overflow-y-auto flex-1 p-2 space-y-1">
            {employees?.map(f => {
              const temFolha = folhas?.find(folha => folha.funcionarioOrigemId === f.id);
              const isSelected = selectedFuncionario?.id === f.id;
              
              return (
                <button
                  key={f.id}
                  onClick={() => handleSelectFuncionario(f)}
                  className={`w-full text-left p-3 rounded-xl transition-all border ${
                    isSelected 
                      ? 'bg-blue-500/10 border-blue-500/30' 
                      : 'border-transparent hover:bg-slate-700/30'
                  }`}
                >
                  <div className="flex justify-between items-center">
                    <div>
                      <div className={`font-semibold text-sm ${isSelected ? 'text-blue-400' : 'text-slate-200'}`}>
                        {f.nome}
                      </div>
                      <div className="text-xs text-slate-500">{f.cargo || 'Sem Cargo'} • {f.tipoContrato || 'CLT'}</div>
                    </div>
                    {temFolha && (
                      <span title="Folha Gerada" className="text-green-400">
                        <CheckCircle2 className="w-4 h-4" />
                      </span>
                    )}
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Painel Direito: Simulador de Holerite */}
        <div className="lg:col-span-3">
          {selectedFuncionario ? (
            <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-xl h-full flex flex-col">
              {/* Header do Holerite */}
              <div className="p-6 border-b border-slate-700 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 bg-slate-800/50">
                <div>
                  <h2 className="text-2xl font-bold text-white mb-1 group flex items-center gap-2">
                    <FileText className="w-6 h-6 text-slate-400" /> Demonstrativo de Pagamento
                  </h2>
                  <p className="text-sm text-slate-400">
                    <span className="font-semibold text-blue-400">{selectedFuncionario.nome}</span> • Competência {selectedMes.split('-').reverse().join('/')}
                  </p>
                </div>
                
                <div className="flex items-center gap-3">
                  {folhaAtual && (
                    <span className={`px-3 py-1 text-xs font-bold uppercase tracking-wider rounded-lg border ${statusColors[folhaAtual.status] || statusColors['FECHADA']}`}>
                      {folhaAtual.status}
                    </span>
                  )}
                  
                  <button 
                    onClick={handleProcessarHolerite}
                    disabled={isProcessing}
                    className="px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 text-white rounded-xl font-medium shadow-lg shadow-blue-500/20 flex items-center gap-2 transition-all"
                  >
                    {isProcessing ? 'Calculando...' : 'Processar Cálculos'}
                  </button>
                  <button className="p-2 border border-slate-600 text-slate-300 hover:bg-slate-700 rounded-xl transition-all" title="Imprimir Recibo">
                    <Printer className="w-5 h-5" />
                  </button>
                </div>
              </div>

              {/* Corpo do Holerite */}
              {folhaAtual ? (
                <div className="p-6 flex-1 flex flex-col">
                  {/* Informações Base */}
                  <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    <div className="bg-slate-900 border border-slate-700 rounded-xl p-4">
                      <div className="text-[10px] text-slate-500 uppercase font-bold tracking-wider mb-1">Salário Base</div>
                      <div className="text-lg font-bold text-slate-200">
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(folhaAtual.salarioBase || 0)}
                      </div>
                    </div>
                    <div className="bg-slate-900 border border-slate-700 rounded-xl p-4">
                      <div className="text-[10px] text-slate-500 uppercase font-bold tracking-wider mb-1">Dias Trabalhados</div>
                      <div className="text-lg font-bold text-slate-200">{folhaAtual.diasTrabalhados} / {folhaAtual.diasUteisBase}</div>
                    </div>
                    <div className="bg-slate-900 border border-slate-700 rounded-xl p-4">
                      <div className="text-[10px] text-slate-500 uppercase font-bold tracking-wider mb-1">Total Proventos</div>
                      <div className="text-lg font-bold text-green-400">
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(folhaAtual.totalProventos || 0)}
                      </div>
                    </div>
                    <div className="bg-slate-900 border border-slate-700 rounded-xl p-4">
                      <div className="text-[10px] text-slate-500 uppercase font-bold tracking-wider mb-1">Total Descontos</div>
                      <div className="text-lg font-bold text-red-400">
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(folhaAtual.totalDescontos || 0)}
                      </div>
                    </div>
                  </div>

                  {/* Tabela de Verbas (Itens da Folha) */}
                  <div className="border border-slate-700 rounded-xl overflow-hidden flex-1 bg-slate-900/50">
                    <table className="w-full text-sm text-left">
                      <thead className="bg-slate-800/80 border-b border-slate-700 text-slate-400">
                        <tr>
                          <th className="p-3">Descrição da Verba</th>
                          <th className="p-3 text-center">Referência</th>
                          <th className="p-3 text-right">Vencimentos (+)</th>
                          <th className="p-3 text-right">Descontos (-)</th>
                        </tr>
                      </thead>
                      <tbody>
                        {folhaAtual.itens?.map((item: any, idx: number) => (
                          <tr key={idx} className="border-b border-slate-700/30 hover:bg-slate-800/50">
                            <td className="p-3 font-medium text-slate-300">{item.descricao}</td>
                            <td className="p-3 text-center text-slate-500">{item.referencia || '-'}</td>
                            <td className="p-3 text-right text-green-400">
                              {item.tipo === 'PROVENTO' ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valor) : ''}
                            </td>
                            <td className="p-3 text-right text-red-400">
                              {item.tipo === 'DESCONTO' ? new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valor) : ''}
                            </td>
                          </tr>
                        ))}
                        {(!folhaAtual.itens || folhaAtual.itens.length === 0) && (
                          <tr>
                            <td colSpan={4} className="p-8 text-center text-slate-500">
                              O demonstrativo processou os cálculos mas não detalhou os itens.
                              Proventos e Depreciações Base já aplicados na soma.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>

                  {/* Rodapé: Líquido a Receber */}
                  <div className="mt-6 flex flex-col sm:flex-row justify-end items-center gap-6 p-4 rounded-xl bg-gradient-to-r from-blue-900/20 to-indigo-900/20 border border-blue-500/20">
                    <div className="text-right">
                      <div className="text-sm font-semibold text-blue-200 mb-1 flex items-center gap-2 justify-end">
                        <HandCoins className="w-4 h-4" /> Líquido a Receber
                      </div>
                      <div className="text-4xl font-black text-white">
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(folhaAtual.salarioLiquido || 0)}
                      </div>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="flex-1 flex flex-col items-center justify-center p-8 text-center">
                  <div className="w-20 h-20 rounded-full bg-slate-700/50 flex items-center justify-center mb-4">
                    <AlertCircle className="w-10 h-10 text-slate-500" />
                  </div>
                  <h4 className="text-xl font-bold text-slate-300 mb-2">Folha não gerada</h4>
                  <p className="text-slate-500 max-w-sm mb-6">
                    Ainda não existe um demonstrativo processado para <b>{selectedFuncionario.nome}</b> neste mês. 
                  </p>
                  <button 
                    onClick={handleProcessarHolerite}
                    disabled={isProcessing}
                    className="px-6 py-3 bg-blue-500 hover:bg-blue-600 text-white rounded-xl font-medium shadow-lg shadow-blue-500/20 transition-all hover:-translate-y-1"
                  >
                    {isProcessing ? 'Iniciando Setup...' : 'Gerar Primeiro Demonstrativo'}
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-xl h-full flex flex-col items-center justify-center p-12 text-center">
               <div className="w-24 h-24 rounded-full bg-slate-700/30 flex items-center justify-center mb-6 ring-4 ring-slate-700">
                  <Calculator className="w-12 h-12 text-blue-500/50" />
               </div>
               <h3 className="text-2xl font-bold text-slate-400 mb-2">Simulador de Folha</h3>
               <p className="text-slate-500">Selecione um funcionário no painel à esquerda para abrir o demonstrativo de cálculos.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
