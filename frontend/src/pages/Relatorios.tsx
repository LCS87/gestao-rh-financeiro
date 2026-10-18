import { useState } from 'react';
import {
  useExtratoProvisoes,
  useTurnoverAbsenteismo,
  useDRE,
  useSaldoAtual,
  baixarRelatorio,
} from '../hooks/useComissoesRelatorios';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { FileBarChart2, Download, FileText, Wallet, TrendingDown, Sheet } from 'lucide-react';

const mesAtual = () => new Date().toISOString().slice(0, 7);
const fmtBRL = (v: number | undefined | null) =>
  v == null || Number.isNaN(Number(v))
    ? '—'
    : Number(v).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const fmtPct = (v: number | undefined | null) => (v == null ? '—' : `${Number(v).toFixed(2)}%`);

type Aba = 'provisoes' | 'turnover' | 'dre';

export function Relatorios() {
  const { showToast } = useToast();
  const [aba, setAba] = useState<Aba>('provisoes');
  const [periodo, setPeriodo] = useState(mesAtual());
  const [dreInicio, setDreInicio] = useState(`${new Date().getFullYear()}-01`);
  const [dreFim, setDreFim] = useState(mesAtual());

  const { data: provisoes, isLoading: loadProv } = useExtratoProvisoes(periodo);
  const { data: turnover, isLoading: loadTurn } = useTurnoverAbsenteismo(periodo);
  const { data: dre, isLoading: loadDre } = useDRE(dreInicio, dreFim);
  const { data: saldo } = useSaldoAtual();

  const input =
    'bg-slate-800 border border-slate-700 rounded px-3 py-2 text-sm text-slate-100 focus:outline-none focus:border-blue-500';

  const exportar = (path: string, params: Record<string, string>, filename: string) =>
    baixarRelatorio(path, params, filename).catch(() => showToast('Erro ao exportar relatório.', 'error'));

  const abas: Array<{ id: Aba; label: string }> = [
    { id: 'provisoes', label: 'Provisões da Folha' },
    { id: 'turnover', label: 'Turnover & Absenteísmo' },
    { id: 'dre', label: 'DRE Gerencial' },
  ];

  return (
    <div className="p-6 space-y-6">
      <header className="flex items-center justify-between flex-wrap gap-3">
        <div className="flex items-center gap-3">
          <FileBarChart2 className="w-7 h-7 text-cyan-400" />
          <h1 className="text-2xl font-bold text-white">Central de Relatórios</h1>
        </div>
        <div className="flex items-center gap-2 bg-slate-900 border border-emerald-700/50 rounded-lg px-4 py-2 text-sm">
          <Wallet className="w-4 h-4 text-emerald-400" />
          <span className="text-slate-400">Saldo atual:</span>
          <span className="font-bold text-emerald-400">{fmtBRL(saldo)}</span>
        </div>
      </header>

      <nav className="flex gap-2 flex-wrap">
        {abas.map((a) => (
          <button
            key={a.id}
            onClick={() => setAba(a.id)}
            className={`px-4 py-2 rounded-lg text-sm font-medium ${aba === a.id ? 'bg-blue-600 text-white' : 'bg-slate-800 text-slate-300'}`}
          >
            {a.label}
          </button>
        ))}
      </nav>

      {aba === 'provisoes' && (
        <section className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-white">
              <Sheet className="w-5 h-5 text-pink-400" /> Extrato de Provisões — Folha de Pagamento
            </h2>
            <div className="flex gap-2">
              <input type="month" value={periodo} onChange={(e) => setPeriodo(e.target.value)} className={input} />
              <button
                onClick={() => exportar('/relatorios/provisoes-folha/export', { periodo }, `provisoes-${periodo}.csv`)}
                className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-600 text-white rounded px-3 py-2 text-sm"
              >
                <Download className="w-4 h-4" /> CSV
              </button>
              <button
                onClick={() => exportar('/relatorios/provisoes-folha/export/pdf', { periodo }, `provisoes-${periodo}.pdf`)}
                className="flex items-center gap-2 bg-rose-700 hover:bg-rose-600 text-white rounded px-3 py-2 text-sm"
              >
                <FileText className="w-4 h-4" /> PDF
              </button>
            </div>
          </div>
          {loadProv ? (
            <PageLoading text="Calculando provisões..." />
          ) : (
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-sm">
              {[
                { label: 'Provisão FGTS', value: provisoes?.provisaoFGTS },
                { label: 'Provisão INSS', value: provisoes?.provisaoINSS },
                { label: 'Provisão 13º', value: provisoes?.provisaoDecimoTerceiro },
                { label: 'Provisão Férias', value: provisoes?.provisaoFerias },
                { label: 'Comissões a pagar', value: provisoes?.comissoesAPagar },
                { label: 'Encargos estimados', value: provisoes?.encargosEstimados },
                { label: 'Total provisionado', value: provisoes?.totalProvisionado },
                { label: 'Folhas no período', value: provisoes?.quantidadeFolhas as unknown as number },
              ].map((c) => (
                <div key={c.label} className="bg-slate-800 rounded-lg p-4">
                  <p className="text-slate-400">{c.label}</p>
                  <p className={`text-lg font-bold ${c.label.includes('Total') ? 'text-pink-400' : 'text-white'}`}>
                    {c.label.includes('Folhas') ? c.value ?? 0 : fmtBRL(c.value)}
                  </p>
                </div>
              ))}
            </div>
          )}
        </section>
      )}

      {aba === 'turnover' && (
        <section className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <h2 className="flex items-center gap-2 text-lg font-semibold text-white">
              <TrendingDown className="w-5 h-5 text-orange-400" /> Turnover e Absenteísmo por Departamento
            </h2>
            <div className="flex gap-2">
              <input type="month" value={periodo} onChange={(e) => setPeriodo(e.target.value)} className={input} />
              <button
                onClick={() =>
                  exportar('/relatorios/turnover-absenteismo/export', { periodo }, `turnover-${periodo}.csv`)
                }
                className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-600 text-white rounded px-3 py-2 text-sm"
              >
                <Download className="w-4 h-4" /> CSV
              </button>
              <button
                onClick={() => exportar('/relatorios/turnover-absenteismo/export/pdf', { periodo }, `turnover-${periodo}.pdf`)}
                className="flex items-center gap-2 bg-rose-700 hover:bg-rose-600 text-white rounded px-3 py-2 text-sm"
              >
                <FileText className="w-4 h-4" /> PDF
              </button>
            </div>
          </div>
          {loadTurn ? (
            <PageLoading text="Calculando indicadores..." />
          ) : (
            <table className="w-full text-sm">
              <thead className="bg-slate-800 text-slate-400">
                <tr>
                  <th className="text-left px-4 py-3">Departamento</th>
                  <th className="text-right px-4 py-3">Efetivo</th>
                  <th className="text-right px-4 py-3">Desligados</th>
                  <th className="text-right px-4 py-3">Turnover</th>
                  <th className="text-right px-4 py-3">Dias afastamento</th>
                  <th className="text-right px-4 py-3">Absenteísmo</th>
                </tr>
              </thead>
              <tbody>
                {(turnover ?? []).length === 0 && (
                  <tr>
                    <td colSpan={6} className="text-center text-slate-500 py-8">
                      Sem dados no período.
                    </td>
                  </tr>
                )}
                {(turnover ?? []).map((l) => (
                  <tr key={l.departamentoId} className="border-t border-slate-800 text-slate-200">
                    <td className="px-4 py-3 font-mono text-xs">{String(l.departamentoId).slice(0, 8)}…</td>
                    <td className="px-4 py-3 text-right">{Number(l.totalFuncionarios)}</td>
                    <td className="px-4 py-3 text-right">{Number(l.desligadosNoPeriodo)}</td>
                    <td className="px-4 py-3 text-right font-semibold text-orange-400">{fmtPct(l.turnoverPercent as number)}</td>
                    <td className="px-4 py-3 text-right">{Number(l.diasAfastamento)}</td>
                    <td className="px-4 py-3 text-right font-semibold text-yellow-400">{fmtPct(l.absenteismoPercent as number)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      )}

      {aba === 'dre' && (
        <section className="bg-slate-900 border border-slate-800 rounded-xl p-5 space-y-4">
          <div className="flex items-center justify-between flex-wrap gap-3">
            <h2 className="text-lg font-semibold text-white">DRE Gerencial (Demonstrativo de Resultado)</h2>
            <div className="flex gap-2">
              <input type="month" value={dreInicio} onChange={(e) => setDreInicio(e.target.value)} className={input} />
              <input type="month" value={dreFim} onChange={(e) => setDreFim(e.target.value)} className={input} />
              <button
                onClick={() =>
                  exportar('/relatorios/dre/export', { periodoInicio: dreInicio, periodoFim: dreFim }, `dre-${dreInicio}-${dreFim}.csv`)
                }
                className="flex items-center gap-2 bg-emerald-700 hover:bg-emerald-600 text-white rounded px-3 py-2 text-sm"
              >
                <Download className="w-4 h-4" /> CSV
              </button>
              <button
                onClick={() =>
                  exportar('/relatorios/dre/export/pdf', { periodoInicio: dreInicio, periodoFim: dreFim }, `dre-${dreInicio}-${dreFim}.pdf`)
                }
                className="flex items-center gap-2 bg-rose-700 hover:bg-rose-600 text-white rounded px-3 py-2 text-sm"
              >
                <FileText className="w-4 h-4" /> PDF
              </button>
            </div>
          </div>
          {loadDre ? (
            <PageLoading text="Carregando DRE..." />
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-slate-800 text-slate-400">
                  <tr>
                    <th className="text-left px-4 py-3">Período</th>
                    <th className="text-right px-4 py-3">Receita Total</th>
                    <th className="text-right px-4 py-3">Custo Vendas</th>
                    <th className="text-right px-4 py-3">Lucro Bruto</th>
                    <th className="text-right px-4 py-3">Despesas</th>
                    <th className="text-right px-4 py-3">Lucro Operacional</th>
                    <th className="text-right px-4 py-3">Impostos</th>
                    <th className="text-right px-4 py-3">Lucro Líquido</th>
                  </tr>
                </thead>
                <tbody>
                  {(dre ?? []).length === 0 && (
                    <tr>
                      <td colSpan={8} className="text-center text-slate-500 py-8">
                        Nenhum DRE gerado no período. Gere pelo backend ou agendamento automático.
                      </td>
                    </tr>
                  )}
                  {(dre ?? []).map((d) => (
                    <tr key={d.periodo} className="border-t border-slate-800 text-slate-200">
                      <td className="px-4 py-3">{d.periodo}</td>
                      <td className="px-4 py-3 text-right text-emerald-400">{fmtBRL(d.receitaTotal)}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(d.custoVendas)}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(d.lucroBruto)}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(d.despesasTotal)}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(d.lucroOperacional)}</td>
                      <td className="px-4 py-3 text-right">{fmtBRL(d.impostos)}</td>
                      <td className={`px-4 py-3 text-right font-bold ${Number(d.lucroLiquido) >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                        {fmtBRL(d.lucroLiquido)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      )}
    </div>
  );
}

export default Relatorios;
