import React from 'react';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import { Input } from '../components/ui/Input';
import { useSalesData, useAddSale, useHistoricoPessoal } from '../hooks/useApi';
import { useQuery } from '@tanstack/react-query';
import { api } from '../lib/api';
import { PageLoading } from '../components/Layout';
import { LineChart as LineChartIcon, Trophy, BarChart3 } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import type { Vendedor } from '../types';
import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  Legend 
} from 'recharts';

export function VendasPage() {
  const { user } = useAuth();
  const { data: salesData, isLoading } = useSalesData();

  // Busca META_VENDAS_DIARIA das configurações
  const { data: parametrosData } = useQuery({
    queryKey: ['parametrosSistema'],
    queryFn: async () => {
      const { data } = await api.get('/api/parametros');
      return data;
    },
  });

  const metaValue = parametrosData?.find((p: any) => p.chave === 'META_VENDAS_DIARIA')?.valor;
  const GOAL = metaValue ? parseFloat(metaValue) : 1400;

  const addSale = useAddSale();

  const [selectedSeller, setSelectedSeller] = React.useState('');
  const [saleAmount, setSaleAmount] = React.useState('');

  const [daysLimit, setDaysLimit] = React.useState<number>(30);

  const { data: historicoPessoal } = useHistoricoPessoal(user?.id, daysLimit);

  if (isLoading) {
    return <PageLoading text="Carregando vendas..." />;
  }

  const handleAddSale = async () => {
    if (selectedSeller && saleAmount) {
      try {
        await addSale.mutateAsync({
          sellerId: selectedSeller,
          amount: parseFloat(saleAmount),
        });
        setSelectedSeller('');
        setSaleAmount('');
        console.log('Venda adicionada com sucesso!');
      } catch (error) {
        console.error('Erro ao registrar venda:', error);
      }
    }
  };

  // Ordenar por vendas diárias (decrescente)
  const sortedSales = [...(salesData || [])].sort((a, b) => b.vendasDiarias - a.vendasDiarias);
  
  // Dados do usuário atual (se aplicável)
  const myData = sortedSales.find(v => v.nome === user?.name || v.id.toString() === user?.id);

  const chartDataToShow = historicoPessoal || [];

  return (
    <div className="space-y-6 max-w-[1600px] mx-auto">
      <div>
        <h1 className="text-3xl font-bold text-white flex items-center gap-2">
          <LineChartIcon className="w-8 h-8 text-blue-400" /> Vendas
        </h1>
        <p className="text-slate-300">
          {user?.level === 'user' ? 'Seu desempenho e histórico de vendas' : 'Controle de vendas e metas da equipe'}
        </p>
      </div>

      {/* Adicionar Venda (Somente para TI, Admin, Supervisor) */}
      {user?.level !== 'user' && (
        <Card>
          <CardHeader>
            <CardTitle>Registrar Venda</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <div>
                <label htmlFor="vendedor" className="block text-sm font-medium text-slate-300 mb-2">
                  Vendedor
                </label>
                <select
                  id="vendedor"
                  value={selectedSeller}
                  onChange={(e) => setSelectedSeller(e.target.value)}
                  className="w-full px-3 py-2 bg-slate-700 text-white rounded border border-slate-600 focus:border-blue-500 focus:outline-none"
                >
                  <option value="">Selecione um vendedor</option>
                  {salesData?.map((vendedor: Vendedor) => (
                    <option key={vendedor.id} value={vendedor.id}>
                      {vendedor.nome} - Meta(1400)
                    </option>
                  ))}
                </select>
              </div>

              <Input
                label="Valor da Venda"
                type="number"
                value={saleAmount}
                onChange={(e) => setSaleAmount(e.target.value)}
                placeholder="0.00"
                step="0.01"
              />

              <div className="flex items-end">
                <Button
                  onClick={handleAddSale}
                  disabled={!selectedSeller || !saleAmount || addSale.isPending}
                  className="w-full"
                >
                  {addSale.isPending ? 'Registrando...' : 'Registrar Venda'}
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Visão do Vendedor (User) */}
      {user?.level === 'user' && (
        <Card className="bg-slate-800 border-slate-700">
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle className="flex items-center gap-2">
              <BarChart3 className="w-5 h-5 text-blue-400" /> Meu Desempenho
            </CardTitle>
            <select
              value={daysLimit}
              onChange={(e) => setDaysLimit(Number(e.target.value))}
              className="px-3 py-1.5 bg-slate-700 text-white text-sm rounded border border-slate-600 focus:border-blue-500 focus:outline-none"
            >
              <option value={15}>15 Dias</option>
              <option value={30}>30 Dias</option>
              <option value={60}>60 Dias</option>
            </select>
          </CardHeader>
          <CardContent>
            {myData && (
              <div className="flex flex-col md:flex-row gap-6 mb-8 p-4 bg-slate-700/30 rounded-xl border border-slate-600/50">
                <div className="flex-1">
                  <div className="text-sm text-slate-400">Meta Diária</div>
                  <div className="text-2xl font-bold text-white">R$ {GOAL.toLocaleString('pt-BR')}</div>
                </div>
                <div className="flex-1 border-t md:border-t-0 md:border-l border-slate-600/50 pt-4 md:pt-0 md:pl-6">
                  <div className="text-sm text-slate-400">Vendas Hoje: R$ {myData.vendasDiarias.toLocaleString()}</div>
                  <div className="w-full bg-slate-700/50 rounded-full h-2 mt-2">
                  <div 
                    className={`h-2 rounded-full transition-all duration-1000 ${
                      myData.vendasDiarias >= GOAL ? 'bg-green-400' : 'bg-blue-400'
                    }`}
                    style={{ width: `${(() => { const totalToday = myData?.vendasDiarias || 0; const progress = Math.min((totalToday / GOAL) * 100, 100); return progress; })()}%` }}
                  ></div>
                </div>
                </div>
                <div className="flex-1 border-t md:border-t-0 md:border-l border-slate-600/50 pt-4 md:pt-0 md:pl-6">
                  <div className="text-sm text-slate-400">Status</div>
                  <div className="text-sm font-medium mt-1">
                    {myData.vendasDiarias >= GOAL 
                      ? <span className="text-green-400 bg-green-400/10 px-2 py-1 rounded">✅ Meta Batida</span>
                      : <span className="text-orange-400 bg-orange-400/10 px-2 py-1 rounded">⏳ Faltam R$ {(GOAL - myData.vendasDiarias).toLocaleString()}</span>
                    }
                  </div>
                </div>
              </div>
            )}
            
            <div className="h-80 w-full mt-4">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={chartDataToShow} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                  <XAxis dataKey="data" stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis stroke="#94a3b8" fontSize={12} tickLine={false} axisLine={false} tickFormatter={(value) => `R$ ${value/1000}k`} />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e293b', border: '1px solid #334155', borderRadius: '8px' }}
                    itemStyle={{ color: '#fff' }}
                    cursor={{ fill: '#334155', opacity: 0.4 }}
                  />
                  <Legend />
                  <Bar dataKey="vendas" name="Vendas" fill="#3b82f6" radius={[4, 4, 0, 0]} maxBarSize={50} />
                  <Bar dataKey="meta" name="Meta" fill="#64748b" radius={[4, 4, 0, 0]} maxBarSize={50} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Ranking (Somente para TI, Admin, Supervisor) */}
      {user?.level !== 'user' && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2"><Trophy className="w-5 h-5 text-yellow-400" /> Ranking Diário da Equipe</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {sortedSales.map((vendedor, index) => (
                <div
                  key={vendedor.id}
                  className="flex items-center justify-between p-4 bg-slate-700 rounded-lg"
                >
                  <div className="flex items-center space-x-4">
                    <div
                      className={`text-2xl font-bold ${index === 0
                          ? 'text-yellow-400'
                          : index === 1
                            ? 'text-gray-300'
                            : index === 2
                              ? 'text-orange-600'
                              : 'text-slate-400'
                        }`}
                    >
                      {index + 1}º
                    </div>
                    <div>
                      <div className="font-medium text-white">{vendedor.nome}</div>
                      <div className="text-sm text-slate-400">
                        Meta: R$ {vendedor.metaDiaria.toLocaleString()}
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div
                      className={`text-lg font-bold ${vendedor.vendasDiarias >= vendedor.metaDiaria
                          ? 'text-green-400'
                          : 'text-red-400'
                        }`}
                    >
                      R$ {vendedor.vendasDiarias.toLocaleString()}
                    </div>
                    <div className="text-xs text-slate-400">
                      {vendedor.vendasDiarias >= vendedor.metaDiaria
                        ? '✅ Meta batida'
                        : `❌ Faltam R$ ${(vendedor.metaDiaria - vendedor.vendasDiarias).toLocaleString()}`}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Resumo */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2"><BarChart3 className="w-5 h-5 text-cyan-400" /> {user?.level === 'user' ? 'Seu Resumo Geral' : 'Resumo Global de Vendas'}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="text-center">
              <div className="text-3xl font-bold text-green-400">
                R$ {user?.level === 'user' ? (myData?.vendasDiarias || 0).toLocaleString() : sortedSales.reduce((sum, v) => sum + v.vendasDiarias, 0).toLocaleString()}
              </div>
              <div className="text-sm text-slate-400 mt-1">Vendas Diárias</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold text-blue-400">
                R$ {user?.level === 'user' ? (myData?.vendasSemanais || 0).toLocaleString() : sortedSales.reduce((sum, v) => sum + v.vendasSemanais, 0).toLocaleString()}
              </div>
              <div className="text-sm text-slate-400 mt-1">Vendas Semanais</div>
            </div>
            <div className="text-center">
              <div className="text-3xl font-bold text-purple-400">
                R$ {user?.level === 'user' ? (myData?.vendasMensais || 0).toLocaleString() : sortedSales.reduce((sum, v) => sum + v.vendasMensais, 0).toLocaleString()}
              </div>
              <div className="text-sm text-slate-400 mt-1">Vendas Mensais</div>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
