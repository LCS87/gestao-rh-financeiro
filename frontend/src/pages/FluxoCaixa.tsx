import { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useContasPagar, useContasReceber, useCreateContaPagar, useCreateContaReceber, queryClient } from '../hooks/useApi';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { api } from '../lib/api';
import { Shield, ArrowUpRight, ArrowDownRight, DollarSign, Wallet, CheckCircle2, Clock, Plus, Search, X } from 'lucide-react';

export function FluxoCaixa() {
  const { hasPermission } = useAuth();
  const { showToast } = useToast();
  
  const [activeTab, setActiveTab] = useState<'pagar' | 'receber'>('receber');
  const [searchTerm, setSearchTerm] = useState('');
  
  // Modals state
  const [showModalPagar, setShowModalPagar] = useState(false);
  const [showModalReceber, setShowModalReceber] = useState(false);
  
  const [formData, setFormData] = useState({
    parceiro: '',
    descricao: '',
    valorTotal: '',
    dataVencimento: '',
    categoria: 'OUTROS'
  });

  const createContaPagar = useCreateContaPagar();
  const createContaReceber = useCreateContaReceber();

  const resetForm = () => {
    setFormData({ parceiro: '', descricao: '', valorTotal: '', dataVencimento: '', categoria: 'OUTROS' });
  };

  const handleCreate = async (tipo: 'pagar' | 'receber', e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (tipo === 'pagar') {
        await createContaPagar.mutateAsync({
          fornecedor: formData.parceiro,
          descricao: formData.descricao,
          valorTotal: Number(formData.valorTotal),
          dataVencimento: formData.dataVencimento,
          categoria: formData.categoria
        });
        showToast('Despesa lançada!', 'success');
        setShowModalPagar(false);
      } else {
        await createContaReceber.mutateAsync({
          cliente: formData.parceiro,
          descricao: formData.descricao,
          valorTotal: Number(formData.valorTotal),
          dataVencimento: formData.dataVencimento,
          categoria: formData.categoria
        });
        showToast('Receita lançada!', 'success');
        setShowModalReceber(false);
      }
      resetForm();
    } catch {
      showToast('Erro ao salvar conta.', 'error');
    }
  };
  const { data: contasPagar, isLoading: loadingPagar } = useContasPagar();
  const { data: contasReceber, isLoading: loadingReceber } = useContasReceber();

  if (!hasPermission('financeiro')) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="text-center space-y-4">
          <Shield className="w-16 h-16 text-slate-600 mx-auto" />
          <h2 className="text-2xl font-bold text-white">Acesso Restrito</h2>
          <p className="text-slate-400">Esta área requer privilégios do setor Financeiro.</p>
        </div>
      </div>
    );
  }

  if (loadingPagar || loadingReceber) {
    return <PageLoading text="Carregando fluxo de caixa..." />;
  }

  const contasPagarFiltradas = contasPagar?.filter(c => 
    c.fornecedor.toLowerCase().includes(searchTerm.toLowerCase()) || 
    c.descricao.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const contasReceberFiltradas = contasReceber?.filter(c => 
    c.cliente.toLowerCase().includes(searchTerm.toLowerCase()) || 
    c.descricao.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Cálculos base para cabeçalho
  const aReceberPendentes = contasReceber?.filter(c => c.status !== 'PAGO' && c.status !== 'CANCELADO') || [];
  const aPagarPendentes = contasPagar?.filter(c => c.status !== 'PAGO' && c.status !== 'CANCELADO') || [];
  
  const totalReceber = aReceberPendentes.reduce((acc, c) => acc + (c.valorTotal - c.valorRecebido), 0);
  const totalPagar = aPagarPendentes.reduce((acc, c) => acc + (c.valorTotal - c.valorPago), 0);
  const saldoPrevisto = totalReceber - totalPagar;

  const getStatusColor = (status: string) => {
    switch(status) {
      case 'PAGO': return 'bg-green-500/10 text-green-400 border-green-500/20';
      case 'PARCIAL': return 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20';
      case 'CANCELADO': return 'bg-slate-500/10 text-slate-400 border-slate-500/20';
      default: return 'bg-blue-500/10 text-blue-400 border-blue-500/20';
    }
  };

  const handleBaixa = async (tipo: 'pagar' | 'receber', id: string) => {
    try {
      if (tipo === 'pagar') {
        await api.post(`/financeiro/contas-pagar/${id}/pagar-total`);
        queryClient.invalidateQueries({ queryKey: ['contasPagar'] });
        showToast('Conta a pagar baixada com sucesso.', 'success');
      } else {
        await api.post(`/financeiro/contas-receber/${id}/receber-total`);
        queryClient.invalidateQueries({ queryKey: ['contasReceber'] });
        showToast('Recebimento baixado com sucesso.', 'success');
      }
    } catch (err) {
      showToast('Erro ao realizar a baixa.', 'error');
    }
  };

  const renderStats = () => (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
      <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-xl relative overflow-hidden group">
        <div className="absolute -right-4 -top-4 w-24 h-24 bg-green-500/10 rounded-full blur-2xl group-hover:bg-green-500/20 transition-all" />
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-400 mb-1">A Receber (Pendentes)</p>
            <h3 className="text-3xl font-bold text-white">
              {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(totalReceber)}
            </h3>
          </div>
          <div className="w-10 h-10 rounded-xl bg-green-500/10 flex items-center justify-center">
            <ArrowUpRight className="w-5 h-5 text-green-400" />
          </div>
        </div>
      </div>

      <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-xl relative overflow-hidden group">
        <div className="absolute -right-4 -top-4 w-24 h-24 bg-red-500/10 rounded-full blur-2xl group-hover:bg-red-500/20 transition-all" />
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-400 mb-1">A Pagar (Pendentes)</p>
            <h3 className="text-3xl font-bold text-white">
              {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(totalPagar)}
            </h3>
          </div>
          <div className="w-10 h-10 rounded-xl bg-red-500/10 flex items-center justify-center">
            <ArrowDownRight className="w-5 h-5 text-red-400" />
          </div>
        </div>
      </div>

      <div className="bg-slate-800 rounded-2xl p-6 border border-slate-700 shadow-xl relative overflow-hidden group">
        <div className={`absolute -right-4 -top-4 w-24 h-24 ${saldoPrevisto >= 0 ? 'bg-blue-500/10 group-hover:bg-blue-500/20' : 'bg-orange-500/10 group-hover:bg-orange-500/20'} rounded-full blur-2xl transition-all`} />
        <div className="flex justify-between items-start">
          <div>
            <p className="text-sm font-medium text-slate-400 mb-1">Saldo Final Previsto</p>
            <h3 className={`text-3xl font-bold ${saldoPrevisto >= 0 ? 'text-blue-400' : 'text-orange-400'}`}>
              {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(saldoPrevisto)}
            </h3>
          </div>
          <div className={`w-10 h-10 rounded-xl ${saldoPrevisto >= 0 ? 'bg-blue-500/10' : 'bg-orange-500/10'} flex items-center justify-center`}>
            <Wallet className={`w-5 h-5 ${saldoPrevisto >= 0 ? 'text-blue-400' : 'text-orange-400'}`} />
          </div>
        </div>
      </div>
    </div>
  );

  return (
    <div className="max-w-[1600px] mx-auto space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white flex items-center gap-3">
            <DollarSign className="w-8 h-8 text-emerald-400 bg-emerald-400/10 rounded-lg p-1" />
            Fluxo de Caixa
          </h1>
          <p className="text-slate-400 mt-1">Gestão de contas a pagar, receber e saldo diário.</p>
        </div>
        <div className="flex gap-3 w-full md:w-auto">
          <button 
            onClick={() => { setShowModalPagar(true); resetForm(); }}
            className="flex-1 md:flex-none px-4 py-2 bg-gradient-to-r from-red-500 to-rose-600 hover:from-red-600 hover:to-rose-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-red-500/20 transition-all border border-red-500/50"
          >
            <Plus className="w-4 h-4" /> Nova Despesa
          </button>
          <button 
            onClick={() => { setShowModalReceber(true); resetForm(); }}
            className="flex-1 md:flex-none px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white rounded-xl font-medium flex items-center justify-center gap-2 shadow-lg shadow-green-500/20 transition-all border border-green-500/50"
          >
            <Plus className="w-4 h-4" /> Nova Receita
          </button>
        </div>
      </div>

      {renderStats()}

      <div className="bg-slate-800 rounded-2xl border border-slate-700 overflow-hidden shadow-xl">
        {/* Toolbar & Tabs */}
        <div className="p-4 border-b border-slate-700 flex flex-col md:flex-row justify-between items-center gap-4 bg-slate-800/50">
          <div className="flex bg-slate-900 rounded-xl p-1 border border-slate-700 w-full md:w-auto h-[46px]">
            <button
              className={`flex-1 md:px-8 py-2 rounded-lg text-sm font-semibold transition-all ${
                activeTab === 'receber' ? 'bg-slate-700 text-white shadow-md' : 'text-slate-400 hover:text-slate-300'
              }`}
              onClick={() => setActiveTab('receber')}
            >
              A Receber
            </button>
            <button
              className={`flex-1 md:px-8 py-2 rounded-lg text-sm font-semibold transition-all ${
                activeTab === 'pagar' ? 'bg-slate-700 text-white shadow-md' : 'text-slate-400 hover:text-slate-300'
              }`}
              onClick={() => setActiveTab('pagar')}
            >
              A Pagar
            </button>
          </div>
          <div className="relative w-full md:w-64">
            <Search className="w-4 h-4 absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input 
              type="text" 
              placeholder="Buscar título / nome..." 
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-9 pr-4 py-2 text-sm text-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition-all"
            />
          </div>
        </div>

        {/* Table View */}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-900/80 border-b border-slate-700 text-slate-400 uppercase text-[10px] font-bold tracking-wider">
              <tr>
                <th className="p-4">Vencimento</th>
                <th className="p-4">{activeTab === 'pagar' ? 'Fornecedor' : 'Cliente'} / Descrição</th>
                <th className="p-4">Categoria</th>
                <th className="p-4 text-right">Valor Bruto</th>
                <th className="p-4 text-center">Status</th>
                <th className="p-4 text-right">Ações</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-700/50">
              {(activeTab === 'pagar' ? contasPagarFiltradas : contasReceberFiltradas)?.map((item: any) => {
                const isPagar = activeTab === 'pagar';
                const parceiro = isPagar ? item.fornecedor : item.cliente;
                const valorRestante = item.valorTotal - (isPagar ? item.valorPago : item.valorRecebido);
                const isPendenteVencido = item.status === 'PENDENTE' && new Date(item.dataVencimento) < new Date();
                
                return (
                  <tr key={item.id} className="hover:bg-slate-700/20 transition-colors group">
                    <td className="p-4">
                      <div className="flex items-center gap-2">
                        <Clock className={`w-4 h-4 ${isPendenteVencido ? 'text-red-400' : 'text-slate-500'}`} />
                        <span className={`font-mono ${isPendenteVencido ? 'text-red-400 font-bold' : 'text-slate-300'}`}>
                          {new Date(item.dataVencimento).toLocaleDateString('pt-BR')}
                        </span>
                      </div>
                    </td>
                    <td className="p-4">
                      <div className="font-medium text-white">{parceiro}</div>
                      <div className="text-xs text-slate-500">{item.descricao}</div>
                    </td>
                    <td className="p-4 text-slate-400 text-xs font-semibold">{item.categoria.replace(/_/g, ' ')}</td>
                    <td className="p-4 text-right">
                      <div className="font-bold text-slate-200">
                        {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(item.valorTotal)}
                      </div>
                      {item.status === 'PARCIAL' && (
                        <div className="text-[10px] text-yellow-400">
                          Resta: {new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(valorRestante)}
                        </div>
                      )}
                    </td>
                    <td className="p-4 text-center">
                      <span className={`px-2.5 py-1 rounded text-[10px] uppercase font-black tracking-wider border ${getStatusColor(item.status)}`}>
                        {isPendenteVencido ? 'VENCIDA' : item.status}
                      </span>
                    </td>
                    <td className="p-4 text-right">
                      <button 
                         onClick={() => handleBaixa(activeTab, item.id)}
                         disabled={item.status === 'PAGO' || item.status === 'CANCELADO'}
                         className="p-2 text-slate-400 hover:text-green-400 bg-slate-800 hover:bg-slate-700 border border-slate-700 rounded-lg transition-all disabled:opacity-30 disabled:hover:text-slate-400 disabled:hover:bg-slate-800 group-hover:shadow-lg disabled:cursor-not-allowed"
                         title={isPagar ? 'Dar baixa / Pagar' : 'Confirmar Recebimento'}
                      >
                         <CheckCircle2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                );
              })}
              {((activeTab === 'pagar' && contasPagarFiltradas?.length === 0) || 
                (activeTab === 'receber' && contasReceberFiltradas?.length === 0)) && (
                <tr>
                  <td colSpan={6} className="p-8 text-center text-slate-500">
                    Nenhum registro encontrado nesta categoria.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Renderização Condicional dos Modais */}
      {(showModalPagar || showModalReceber) && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
          <div className="bg-slate-800 rounded-2xl border border-slate-700 shadow-2xl w-full max-w-md overflow-hidden flex flex-col">
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                 {showModalPagar ? <ArrowDownRight className="w-5 h-5 text-red-400" /> : <ArrowUpRight className="w-5 h-5 text-green-400" />}
                 {showModalPagar ? 'Nova Despesa' : 'Nova Receita'}
              </h3>
              <button 
                onClick={() => showModalPagar ? setShowModalPagar(false) : setShowModalReceber(false)} 
                className="text-slate-400 hover:text-white transition-colors"
                type="button"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <form className="p-6 flex flex-col gap-4" onSubmit={(e) => handleCreate(showModalPagar ? 'pagar' : 'receber', e)}>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">{showModalPagar ? 'Fornecedor' : 'Cliente'}</label>
                <input required type="text" value={formData.parceiro} onChange={e => setFormData({...formData, parceiro: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" placeholder={showModalPagar ? "Distribuidora XPTO" : "Cliente XYZ"} />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-300 mb-1">Descrição</label>
                <input required type="text" value={formData.descricao} onChange={e => setFormData({...formData, descricao: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" placeholder="Referência ou motivo..." />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Valor Total (R$)</label>
                  <input required type="number" step="0.01" min="0" value={formData.valorTotal} onChange={e => setFormData({...formData, valorTotal: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500" placeholder="1500.00" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-300 mb-1">Vencimento</label>
                  <input required type="date" value={formData.dataVencimento} onChange={e => setFormData({...formData, dataVencimento: e.target.value})} className="w-full bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500 [color-scheme:dark]" />
                </div>
              </div>
              <button type="submit" disabled={(showModalPagar ? createContaPagar.isPending : createContaReceber.isPending)} className={`mt-4 w-full py-2.5 rounded-lg font-bold text-white transition-colors flex justify-center items-center gap-2 ${showModalPagar ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'} disabled:opacity-50`}>
                Salvar {showModalPagar ? 'Despesa' : 'Receita'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
