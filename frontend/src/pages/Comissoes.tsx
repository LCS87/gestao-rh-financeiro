import { useMemo, useState } from 'react';
import {
  useRegrasComissao,
  useCriarRegraComissao,
  useAtualizarRegraComissao,
  useDeletarRegraComissao,
  useSimularComissao,
  useApuracaoComissoes,
  useAplicarComissoesNaFolha,
  baixarRelatorio,
  type RegraComissao,
} from '../hooks/useComissoesRelatorios';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { Percent, Plus, Trash2, Pencil, Download, FileText, FlaskConical, Calculator, Wallet } from 'lucide-react';

const TIPO_LABELS: Record<string, string> = {
  PORCENTAGEM_VENDA: '% sobre a venda',
  VALOR_FIXO: 'Valor fixo',
  FAIXA_VENDA: 'Faixa de venda',
  BONUS_META: 'Bônus por meta',
};

const mesAtual = () => new Date().toISOString().slice(0, 7);
const fmtBRL = (v: number | undefined | null) =>
  v == null ? '—' : v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });

const REGRA_VAZIA: Partial<RegraComissao> = {
  nome: '',
  descricao: '',
  tipoComissao: 'PORCENTAGEM_VENDA',
  porcentagem: 0,
  valorFixo: undefined,
  valorMinimo: undefined,
  valorMaximo: undefined,
  aceleradorPercentual: undefined,
  metaMinimaPercentual: undefined,
  aplicarPorDepartamento: false,
  ativo: true,
};

export function Comissoes() {
  const { showToast } = useToast();
  const [aba, setAba] = useState<'regras' | 'apuracao'>('regras');
  const [mes, setMes] = useState(mesAtual());
  const [editando, setEditando] = useState<Partial<RegraComissao> & { id?: string } | null>(null);

  // simulador
  const [simRegraId, setSimRegraId] = useState('');
  const [simValor, setSimValor] = useState('1000');
  const [simMeta, setSimMeta] = useState('100');

  const { data: regras, isLoading } = useRegrasComissao();
  const { data: apuracao, isLoading: loadingApuracao } = useApuracaoComissoes(mes);
  const criar = useCriarRegraComissao();
  const atualizar = useAtualizarRegraComissao();
  const deletar = useDeletarRegraComissao();
  const simular = useSimularComissao();
  const aplicarFolha = useAplicarComissoesNaFolha();

  const regrasList = useMemo(() => regras ?? [], [regras]);

  if (isLoading) return <PageLoading text="Carregando regras de comissão..." />;

  const salvarRegra = async () => {
    if (!editando) return;
    if (!editando.nome?.trim()) return showToast('Informe o nome da regra.', 'error');
    try {
      if (editando.id) {
        await atualizar.mutateAsync(editando as RegraComissao);
        showToast('Regra atualizada!', 'success');
      } else {
        await criar.mutateAsync(editando);
        showToast('Regra criada!', 'success');
      }
      setEditando(null);
    } catch {
      showToast('Erro ao salvar regra.', 'error');
    }
  };

  const excluir = async (r: RegraComissao) => {
    if (!window.confirm(`Excluir a regra "${r.nome}"?`)) return;
    try {
      await deletar.mutateAsync(r.id);
      showToast('Regra excluída.', 'success');
    } catch {
      showToast('Erro ao excluir regra.', 'error');
    }
  };

  const rodarSimulacao = async () => {
    if (!simRegraId) return showToast('Selecione uma regra para simular.', 'error');
    try {
      const res = await simular.mutateAsync({
        regraId: simRegraId,
        valorVenda: Number(simValor) || 0,
        percentualMeta: Number(simMeta) || 0,
      });
      const valor = (res as any)?.valorComissao ?? (res as any)?.comissao ?? res;
      showToast(
        `Comissão simulada: ${typeof valor === 'number' ? fmtBRL(valor) : JSON.stringify(valor)}`,
        'success',
      );
    } catch {
      showToast('Erro na simulação.', 'error');
    }
  };

  const input =
    'bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-blue-500';

  return (
    <div className="p-6 space-y-6">
      <header className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-3">
          <Percent className="w-7 h-7 text-emerald-400" />
          <h1 className="text-2xl font-bold text-white">Comissões</h1>
        </div>
        <nav className="flex gap-2">
          <button
            onClick={() => setAba('regras')}
            className={`px-4 py-2 rounded-lg text-sm font-medium ${aba === 'regras' ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-300'}`}
          >
            Regras & Simulador
          </button>
          <button
            onClick={() => setAba('apuracao')}
            className={`px-4 py-2 rounded-lg text-sm font-medium ${aba === 'apuracao' ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-300'}`}
          >
            Apuração Mensal
          </button>
        </nav>
      </header>

      {aba === 'regras' && (
        <>
          {/* Simulador */}
          <section className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-3">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-white">
              <FlaskConical className="w-5 h-5 text-violet-400" /> Simulador de Comissão
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
              <select value={simRegraId} onChange={(e) => setSimRegraId(e.target.value)} className={input}>
                <option value="">Selecione a regra...</option>
                {regrasList.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.nome}
                  </option>
                ))}
              </select>
              <input type="number" placeholder="Valor da venda" value={simValor} onChange={(e) => setSimValor(e.target.value)} className={input} />
              <input type="number" placeholder="% da meta batida" value={simMeta} onChange={(e) => setSimMeta(e.target.value)} className={input} />
              <button
                onClick={rodarSimulacao}
                disabled={simular.isPending}
                className="flex items-center justify-center gap-2 bg-violet-600 hover:bg-violet-500 text-white rounded px-4 py-2 text-sm font-medium disabled:opacity-50"
              >
                <Calculator className="w-4 h-4" /> {simular.isPending ? 'Calculando...' : 'Simular'}
              </button>
            </div>
            <p className="text-xs text-slate-500">
              Aceleradores e bônus de meta são aplicados automaticamente conforme a regra selecionada.
            </p>
          </section>

          {/* Tabela de regras */}
          <section className="bg-slate-900 border border-slate-800 rounded-xl overflow-hidden">
            <div className="flex items-center justify-between p-4 border-b border-slate-800">
              <h2 className="text-lg font-semibold text-white">Regras de Comissão</h2>
              <button
                onClick={() => setEditando({ ...REGRA_VAZIA })}
                className="flex items-center gap-2 bg-emerald-600 hover:bg-emerald-500 text-white rounded px-3 py-2 text-sm font-medium"
              >
                <Plus className="w-4 h-4" /> Nova Regra
              </button>
            </div>
            <table className="w-full text-sm">
              <thead className="bg-slate-800 text-slate-400">
                <tr>
                  <th className="text-left px-4 py-3">Nome</th>
                  <th className="text-left px-4 py-3">Tipo</th>
                  <th className="text-right px-4 py-3">%</th>
                  <th className="text-right px-4 py-3">Valor Fixo</th>
                  <th className="text-right px-4 py-3">Acelerador</th>
                  <th className="text-right px-4 py-3">Meta Mínima</th>
                  <th className="text-center px-4 py-3">Ativa</th>
                  <th className="text-right px-4 py-3">Ações</th>
                </tr>
              </thead>
              <tbody>
                {regrasList.length === 0 && (
                  <tr>
                    <td colSpan={8} className="text-center text-slate-500 py-8">
                      Nenhuma regra cadastrada.
                    </td>
                  </tr>
                )}
                {regrasList.map((r) => (
                  <tr key={r.id} className="border-t border-slate-800 text-slate-200">
                    <td className="px-4 py-3">{r.nome}</td>
                    <td className="px-4 py-3">{TIPO_LABELS[r.tipoComissao] ?? r.tipoComissao}</td>
                    <td className="px-4 py-3 text-right">{r.porcentagem}%</td>
                    <td className="px-4 py-3 text-right">{fmtBRL(r.valorFixo)}</td>
                    <td className="px-4 py-3 text-right">{r.aceleradorPercentual != null ? `+${r.aceleradorPercentual}%` : '—'}</td>
                    <td className="px-4 py-3 text-right">{r.metaMinimaPercentual != null ? `${r.metaMinimaPercentual}%` : '—'}</td>
                    <td className="px-4 py-3 text-center">{r.ativo ? '✅' : '⛔'}</td>
                    <td className="px-4 py-3 text-right space-x-2 whitespace-nowrap">
                      <button onClick={() => setEditando(r)} className="text-blue-400 hover:text-blue-300">
                        <Pencil className="w-4 h-4 inline" />
                      </button>
                      <button onClick={() => excluir(r)} className="text-red-400 hover:text-red-300">
                        <Trash2 className="w-4 h-4 inline" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </section>

          {/* Modal de edição */}
          {editando && (
            <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4" onClick={() => setEditando(null)}>
              <div
                className="bg-slate-900 border border-slate-700 rounded-xl p-6 w-full max-w-lg space-y-3 max-h-[90vh] overflow-y-auto"
                onClick={(e) => e.stopPropagation()}
              >
                <h3 className="text-lg font-semibold text-white">
                  {editando.id ? 'Editar Regra' : 'Nova Regra de Comissão'}
                </h3>
                <input className={`${input} w-full`} placeholder="Nome" value={editando.nome ?? ''} onChange={(e) => setEditando({ ...editando, nome: e.target.value })} />
                <input className={`${input} w-full`} placeholder="Descrição" value={editando.descricao ?? ''} onChange={(e) => setEditando({ ...editando, descricao: e.target.value })} />
                <select
                  className={`${input} w-full`}
                  value={editando.tipoComissao}
                  onChange={(e) => setEditando({ ...editando, tipoComissao: e.target.value as RegraComissao['tipoComissao'] })}
                >
                  {Object.entries(TIPO_LABELS).map(([k, v]) => (
                    <option key={k} value={k}>{v}</option>
                  ))}
                </select>
                <div className="grid grid-cols-2 gap-3">
                  <label className="text-xs text-slate-400">
                    Porcentagem (%)
                    <input type="number" className={`${input} w-full mt-1`} value={editando.porcentagem ?? 0} onChange={(e) => setEditando({ ...editando, porcentagem: Number(e.target.value) })} />
                  </label>
                  <label className="text-xs text-slate-400">
                    Valor fixo (R$)
                    <input type="number" className={`${input} w-full mt-1`} value={editando.valorFixo ?? ''} onChange={(e) => setEditando({ ...editando, valorFixo: e.target.value === '' ? undefined : Number(e.target.value) })} />
                  </label>
                  <label className="text-xs text-slate-400">
                    Faixa mínima (R$)
                    <input type="number" className={`${input} w-full mt-1`} value={editando.valorMinimo ?? ''} onChange={(e) => setEditando({ ...editando, valorMinimo: e.target.value === '' ? undefined : Number(e.target.value) })} />
                  </label>
                  <label className="text-xs text-slate-400">
                    Faixa máxima (R$)
                    <input type="number" className={`${input} w-full mt-1`} value={editando.valorMaximo ?? ''} onChange={(e) => setEditando({ ...editando, valorMaximo: e.target.value === '' ? undefined : Number(e.target.value) })} />
                  </label>
                  <label className="text-xs text-slate-400">
                    Acelerador (%) — extra se meta batida
                    <input type="number" className={`${input} w-full mt-1`} value={editando.aceleradorPercentual ?? ''} onChange={(e) => setEditando({ ...editando, aceleradorPercentual: e.target.value === '' ? undefined : Number(e.target.value) })} />
                  </label>
                  <label className="text-xs text-slate-400">
                    Meta mínima (%) p/ acelerar
                    <input type="number" className={`${input} w-full mt-1`} value={editando.metaMinimaPercentual ?? ''} onChange={(e) => setEditando({ ...editando, metaMinimaPercentual: e.target.value === '' ? undefined : Number(e.target.value) })} />
                  </label>
                </div>
                <label className="flex items-center gap-2 text-sm text-slate-300">
                  <input type="checkbox" checked={editando.ativo ?? true} onChange={(e) => setEditando({ ...editando, ativo: e.target.checked })} /> Regra ativa
                </label>
                <div className="flex justify-end gap-3 pt-2">
                  <button onClick={() => setEditando(null)} className="px-4 py-2 rounded text-sm bg-slate-700 text-slate-200">
                    Cancelar
                  </button>
                  <button
                    onClick={salvarRegra}
                    disabled={criar.isPending || atualizar.isPending}
                    className="px-4 py-2 rounded text-sm bg-blue-600 text-white disabled:opacity-50"
                  >
                    {criar.isPending || atualizar.isPending ? 'Salvando...' : 'Salvar'}
                  </button>
                </div>
              </div>
            </div>
          )}
        </>
      )}

      {aba === 'apuracao' && (
        <section className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <h2 className="text-lg font-semibold text-white">Apuração Mensal de Comissões</h2>
            <div className="flex gap-2">
              <input type="month" value={mes} onChange={(e) => setMes(e.target.value)} className={input} />
              <button
                onClick={() =>
                  baixarRelatorio('/relatorios/comissoes/export', { mesReferencia: mes }, `comissoes-${mes}.csv`).catch(
                    () => showToast('Erro ao exportar CSV.', 'error'),
                  )
                }
                className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-600 text-white rounded px-3 py-2 text-sm"
              >
                <Download className="w-4 h-4" /> Exportar CSV
              </button>
              <button
                onClick={() =>
                  baixarRelatorio('/relatorios/comissoes/export/pdf', { mesReferencia: mes }, `comissoes-${mes}.pdf`).catch(
                    () => showToast('Erro ao exportar PDF.', 'error'),
                  )
                }
                className="flex items-center gap-2 bg-rose-700 hover:bg-rose-600 text-white rounded px-3 py-2 text-sm"
              >
                <FileText className="w-4 h-4" /> Exportar PDF
              </button>
              <button
                disabled={aplicarFolha.isPending}
                onClick={() =>
                  aplicarFolha.mutate(mes, {
                    onSuccess: (msg) => showToast(String(msg), 'success'),
                    onError: () => showToast('Erro ao aplicar comissões na folha.', 'error'),
                  })
                }
                className="flex items-center gap-2 bg-blue-700 hover:bg-blue-600 disabled:opacity-50 text-white rounded px-3 py-2 text-sm"
              >
                <Wallet className="w-4 h-4" />
                {aplicarFolha.isPending ? 'Aplicando...' : 'Aplicar na Folha'}
              </button>
            </div>
          </div>
          {loadingApuracao ? (
            <PageLoading text="Apurando comissões..." />
          ) : (
            <>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm">
                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400">Total de comissões</p>
                  <p className="text-xl font-bold text-emerald-400">{fmtBRL(apuracao?.totalGeralComissoes)}</p>
                </div>
                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400">Vendas consideradas</p>
                  <p className="text-xl font-bold text-white">{apuracao?.quantidadeVendas ?? 0}</p>
                </div>
                <div className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400">Operadores com comissão</p>
                  <p className="text-xl font-bold text-white">{apuracao?.funcionarios?.length ?? 0}</p>
                </div>
              </div>
              <table className="w-full text-sm">
                <thead className="bg-slate-800 text-slate-400">
                  <tr>
                    <th className="text-left px-4 py-3">Operador</th>
                    <th className="text-right px-4 py-3">Vendas</th>
                    <th className="text-right px-4 py-3">Total Vendido</th>
                    <th className="text-right px-4 py-3">Comissão</th>
                  </tr>
                </thead>
                <tbody>
                  {(apuracao?.funcionarios ?? []).length === 0 && (
                    <tr>
                      <td colSpan={4} className="text-center text-slate-500 py-8">
                        Sem comissões apuradas no período.
                      </td>
                    </tr>
                  )}
                  {(apuracao?.funcionarios ?? []).map((f) => (
                    <tr key={f.funcionarioId} className="border-t border-slate-800 text-slate-200">
                      <td className="px-4 py-3">{f.nomeFuncionario}</td>
                      <td className="px-4 py-3 text-right">{f.quantidadeVendas}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(f.totalVendas)}</td>
                      <td className="px-4 py-3 text-right font-semibold text-emerald-400">{fmtBRL(f.totalComissoes)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}
        </section>
      )}
    </div>
  );
}

export default Comissoes;
