import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { useSalesData, useResumoVendas, useHistoricoGlobal } from '../hooks/useApi';
import {
  Zap,
  Building2,
  Trophy,
  Activity,
  Users,
  DollarSign,
  BarChart3,
  CalendarDays,
  Target,
  LineChart,
  Briefcase,
  Award,
  Wrench,
  Crown,
  User,
  TrendingUp,
} from 'lucide-react';
import { 
  LineChart as RechartsLineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer 
} from 'recharts';
import { StatCard } from '../components/StatCard';



export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { data: vendasResumo } = useResumoVendas();
  const { data: vendasRanking } = useSalesData();
  const { data: historicoGlobalArray } = useHistoricoGlobal(30);
  const [showDesempenhoModal, setShowDesempenhoModal] = useState(false);
  const [showMetasModal, setShowMetasModal] = useState(false);

  const recentActivities = [
    {
      id: 1,
      text: 'Novo funcionário cadastrado: João Silva',
      time: '10 min atrás',
      type: 'success',
    },
    { id: 2, text: 'Venda registrada: R$ 5.000', time: '25 min atrás', type: 'info' },
    { id: 3, text: 'Meta mensal atingida - Comercial', time: '1h atrás', type: 'success' },
    { id: 4, text: 'Férias aprovadas: Maria Santos', time: '2h atrás', type: 'warning' },
  ];

  const topSellers = (vendasRanking || []).slice(0, 3).map(seller => ({
    name: seller.nome,
    sales: `R$ ${seller.vendasDiarias.toLocaleString()}`,
    trend: seller.vendasDiarias >= seller.metaDiaria ? '✅ Batida' : '⏳ Atrás'
  }));

  const deptStatus = [
    { name: 'Comercial', active: 12, total: 15, color: 'from-blue-500 to-cyan-500' },
    { name: 'RH e Financeiro', active: 8, total: 8, color: 'from-purple-500 to-pink-500' },
    { name: 'Renovação', active: 6, total: 10, color: 'from-orange-500 to-red-500' },
    { name: 'BKO', active: 5, total: 6, color: 'from-green-500 to-emerald-500' },
    { name: 'TI', active: 4, total: 4, color: 'from-indigo-500 to-violet-500' },
  ];

  // Stat cards config with static classes for Tailwind 4 compatibility
  const adminStatCards = [
    {
      icon: <Users className="w-6 h-6 text-white" />,
      value: '247',
      label: 'Funcionários Ativos',
      trend: '+5%',
      trendColor: 'text-green-400',
      gradient: 'from-blue-500 to-blue-600',
      hoverBorder: 'hover:border-blue-500/50',
      hoverShadow: 'hover:shadow-blue-500/10',
      hoverBg: 'hover:bg-blue-500/5',
    },
    {
      icon: <DollarSign className="w-6 h-6 text-white" />,
      value: `R$ ${(vendasResumo?.totalMensal || 0).toLocaleString()}`,
      label: 'Vendas do Mês',
      trend: '(Base Real)',
      trendColor: 'text-green-400',
      gradient: 'from-green-500 to-green-600',
      hoverBorder: 'hover:border-green-500/50',
      hoverShadow: 'hover:shadow-green-500/10',
      hoverBg: 'hover:bg-green-500/5',
    },
    {
      icon: <BarChart3 className="w-6 h-6 text-white" />,
      value: '2.3%',
      label: 'Taxa de Turnover',
      trend: '-0.5%',
      trendColor: 'text-green-400',
      gradient: 'from-orange-500 to-orange-600',
      hoverBorder: 'hover:border-orange-500/50',
      hoverShadow: 'hover:shadow-orange-500/10',
      hoverBg: 'hover:bg-orange-500/5',
    },
    {
      icon: <CalendarDays className="w-6 h-6 text-white" />,
      value: '4.1%',
      label: 'Absenteísmo',
      trend: '+2%',
      trendColor: 'text-red-400',
      gradient: 'from-red-500 to-red-600',
      hoverBorder: 'hover:border-red-500/50',
      hoverShadow: 'hover:shadow-red-500/10',
      hoverBg: 'hover:bg-red-500/5',
    },
  ];

  const userStatCards = [
    {
      icon: <DollarSign className="w-6 h-6 text-white" />,
      value: `R$ ${((vendasRanking || []).find(v => v.id === user?.id)?.vendasMensais || 0).toLocaleString()}`,
      label: 'Minhas Vendas',
      trend: '(Mês Atual)',
      trendColor: 'text-green-400',
      gradient: 'from-green-500 to-green-600',
      hoverBorder: 'hover:border-green-500/50',
      hoverShadow: 'hover:shadow-green-500/10',
      hoverBg: 'hover:bg-green-500/5',
    },
    {
      icon: <Target className="w-6 h-6 text-white" />,
      value: `R$ ${((vendasRanking || []).find(v => v.id === user?.id)?.metaDiaria || 1400).toLocaleString()}`,
      label: 'Meta Diária',
      trend: '',
      trendColor: 'text-blue-400',
      gradient: 'from-blue-500 to-blue-600',
      hoverBorder: 'hover:border-blue-500/50',
      hoverShadow: 'hover:shadow-blue-500/10',
      hoverBg: 'hover:bg-blue-500/5',
      progress: 85,
    },
    {
      icon: <LineChart className="w-6 h-6 text-white" />,
      value: '24',
      label: 'Vendas Realizadas',
      trend: '3º lugar',
      trendColor: 'text-purple-400',
      gradient: 'from-purple-500 to-purple-600',
      hoverBorder: 'hover:border-purple-500/50',
      hoverShadow: 'hover:shadow-purple-500/10',
      hoverBg: 'hover:bg-purple-500/5',
    },
    {
      icon: <Award className="w-6 h-6 text-white" />,
      value: '92%',
      label: 'Conversão',
      trend: '',
      trendColor: '',
      gradient: 'from-orange-500 to-orange-600',
      hoverBorder: 'hover:border-orange-500/50',
      hoverShadow: 'hover:shadow-orange-500/10',
      hoverBg: 'hover:bg-orange-500/5',
    },
  ];

  const quickActionsAdmin = [
    { to: '/employees', icon: <Users className="w-6 h-6 text-white" />, label: 'Funcionários', color: 'blue' },
    { to: '/sales', icon: <DollarSign className="w-6 h-6 text-white" />, label: 'Vendas', color: 'green' },
    { to: '/departamentos', icon: <Building2 className="w-6 h-6 text-white" />, label: 'Departamentos', color: 'purple' },
    { to: '/cargos', icon: <Briefcase className="w-6 h-6 text-white" />, label: 'Cargos', color: 'orange' },
  ];

  const quickActionsSupervisor = [
    { onClick: () => navigate('/sales'), icon: <DollarSign className="w-6 h-6 text-white" />, label: 'Minhas Vendas', color: 'green' },
    { onClick: () => setShowDesempenhoModal(true), icon: <LineChart className="w-6 h-6 text-white" />, label: 'Desempenho da Equipe', color: 'blue' },
    { onClick: () => setShowMetasModal(true), icon: <Target className="w-6 h-6 text-white" />, label: 'Metas da Equipe', color: 'purple' },
  ];

  const quickActionsUser = [
    { onClick: () => navigate('/sales'), icon: <DollarSign className="w-6 h-6 text-white" />, label: 'Minhas Vendas', color: 'green' },
    { onClick: () => navigate('/sales'), icon: <Target className="w-6 h-6 text-white" />, label: 'Minhas Metas', color: 'purple' },
  ];

  const quickActions = 
    (user?.level === 'ti' || user?.level === 'admin') ? quickActionsAdmin 
    : user?.level === 'supervisor' ? quickActionsSupervisor 
    : quickActionsUser;

  // Helper for static action colors
  const actionColorMap: Record<string, string> = {
    blue: 'from-blue-500/10 to-blue-600/5 border-blue-500/20 hover:border-blue-500/50 hover:bg-blue-500/10 shadow-blue-500/20 from-blue-500 to-blue-600',
    green: 'from-green-500/10 to-green-600/5 border-green-500/20 hover:border-green-500/50 hover:bg-green-500/10 shadow-green-500/20 from-green-500 to-green-600',
    purple: 'from-purple-500/10 to-purple-600/5 border-purple-500/20 hover:border-purple-500/50 hover:bg-purple-500/10 shadow-purple-500/20 from-purple-500 to-purple-600',
    orange: 'from-orange-500/10 to-orange-600/5 border-orange-500/20 hover:border-orange-500/50 hover:bg-orange-500/10 shadow-orange-500/20 from-orange-500 to-orange-600',
  };

  const statCards = user?.level === 'user' ? userStatCards 
    : user?.level === 'supervisor' ? adminStatCards.slice(0, 2)
    : adminStatCards;

  return (
    <div className="space-y-6 md:space-y-8 max-w-[1600px] mx-auto">
      {/* Header Moderno com Gradient */}
      <div className="relative overflow-hidden rounded-3xl bg-slate-800 border border-slate-700 p-6 md:p-10">
        <div className="absolute top-0 right-0 w-full md:w-1/2 h-full bg-gradient-to-l from-blue-500/10 to-transparent pointer-events-none" />
        <div className="relative z-10 flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="flex flex-col md:flex-row items-center gap-6 text-center md:text-left">
            <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center shadow-lg shadow-blue-500/20">
              {user?.level === 'ti' ? (
                <Wrench className="w-8 h-8 text-white" />
              ) : user?.level === 'admin' ? (
                <Crown className="w-8 h-8 text-white" />
              ) : user?.level === 'supervisor' ? (
                <Trophy className="w-8 h-8 text-white" />
              ) : (
                <User className="w-8 h-8 text-white" />
              )}
            </div>
            <div>
              <h1 className="text-3xl md:text-4xl font-bold text-white tracking-tight">Olá, {user?.name}!</h1>
              <p className="text-slate-400 mt-1 text-lg">Bem-vindo ao centro de comando da plataforma</p>
              <div className="flex items-center gap-2 mt-4 flex-wrap justify-center md:justify-start">
                <span className="px-3 py-1 bg-slate-700/50 rounded-full text-xs font-medium text-slate-300 border border-slate-600">
                  {new Date().toLocaleDateString('pt-BR', {
                    weekday: 'long',
                    year: 'numeric',
                    month: 'long',
                    day: 'numeric',
                  })}
                </span>
                <span
                  className={`px-3 py-1 rounded-full text-xs font-bold border ${
                    user?.level === 'ti'
                      ? 'bg-purple-500/20 text-purple-400 border-purple-500/30'
                      : user?.level === 'admin'
                        ? 'bg-blue-500/10 text-blue-400 border-blue-500/30'
                        : user?.level === 'supervisor'
                          ? 'bg-orange-500/10 text-orange-400 border-orange-500/30'
                          : 'bg-green-500/10 text-green-400 border-green-500/30'
                  }`}
                >
                  {user?.level === 'ti' ? 'Nível: Controle Total' :
                   user?.level === 'admin' ? 'Nível: Administrador' :
                   user?.level === 'supervisor' ? 'Nível: Supervisor' : 'Nível: Vendedor'}
                </span>
              </div>
            </div>
          </div>
          <div className="bg-slate-700/30 backdrop-blur rounded-2xl p-4 md:p-6 border border-slate-600/50 min-w-[200px]">
            <p className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-1">
              {user?.level === 'user' ? 'Suas vendas' : 'Faturamento Total'}
            </p>
            <div className="text-3xl font-black text-white">
              R$ {user?.level === 'user' 
                ? ((vendasRanking || []).find(v => v.id === user?.id)?.vendasMensais || 0).toLocaleString() 
                : (vendasResumo?.totalMensal || 0).toLocaleString()}
            </div>
            <div className="flex items-center gap-1 mt-1">
              <TrendingUp className="w-3 h-3 text-green-400" />
              <span className="text-xs text-green-400 font-medium">Tempo Real</span>
            </div>
          </div>
        </div>
      </div>

      {/* Stat Cards Layout - Responsive Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 md:gap-6">
        {statCards.map((card) => (
          <StatCard key={card.label} {...card} />
        ))}
      </div>

      {/* Main Content Sections */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 md:gap-8">
        {/* Left Column (2/3) */}
        <div className="lg:col-span-2 space-y-6 md:space-y-8">
          {/* Quick Actions */}
          <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
            <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <Zap className="w-5 h-5 text-yellow-400 fill-yellow-400/20" />
              Ações Rápidas
            </h2>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
              {quickActions.map((action) => {
                const colors = actionColorMap[action.color].split(' ');
                const gradientFrom = colors[0];
                const gradientTo = colors[1];
                const borderNormal = colors[2];
                const borderHover = colors[3];
                const bgHover = colors[4];
                const shadow = colors[5];
                const iconGradientFrom = colors[6];
                const iconGradientTo = colors[7];

                return (
                  <button
                    key={action.label}
                    onClick={() => {
                      if ('onClick' in action && typeof action.onClick === 'function') {
                        action.onClick();
                      } else if ('to' in action && typeof action.to === 'string') {
                        navigate(action.to);
                      }
                    }}
                    className={`group p-4 rounded-xl bg-gradient-to-br ${gradientFrom} ${gradientTo} border ${borderNormal} ${borderHover} ${bgHover} transition-all duration-300 text-center flex flex-col items-center`}
                  >
                    <div className={`w-12 h-12 mb-3 rounded-xl bg-gradient-to-br ${iconGradientFrom} ${iconGradientTo} flex items-center justify-center shadow-lg ${shadow} group-hover:scale-110 transition-transform`}>
                      {action.icon}
                    </div>
                    <span className="text-xs font-bold text-slate-300 group-hover:text-white uppercase tracking-tight">
                      {action.label}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Status dos Departamentos */}
          {(user?.level === 'ti' || user?.level === 'admin' || user?.level === 'supervisor') && (
            <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
              <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
                <Building2 className="w-5 h-5 text-purple-400" />
                Status dos Departamentos
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                {deptStatus.map((dept) => (
                  <button
                    key={dept.name}
                    onClick={() => navigate('/departamentos')}
                    className="group relative overflow-hidden rounded-xl bg-slate-700/20 border border-slate-600/50 p-4 text-left hover:border-blue-500/50 transition-all duration-300"
                  >
                    <div className="flex justify-between items-start mb-3">
                      <span className="text-sm font-bold text-slate-200">{dept.name}</span>
                      <span className="text-xs font-black text-blue-400">
                        {Math.round((dept.active / dept.total) * 100)}%
                      </span>
                    </div>
                    <div className="w-full h-1.5 bg-slate-700 rounded-full overflow-hidden mb-2">
                      <div
                        className={`h-full bg-gradient-to-r ${dept.color} transition-all duration-300`}
                        style={{ width: `${(dept.active / dept.total) * 100}%` }}
                      />
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-[10px] text-slate-500 uppercase font-bold tracking-wider">Membros: {dept.active}/{dept.total}</span>
                      <Activity className="w-3 h-3 text-slate-600 group-hover:text-blue-400 transition-colors" />
                    </div>
                  </button>
                ))}
              </div>
            </div>
          )}

          {/* Gráfico de Desempenho (Últimos 30 Dias) - ABAIXO dos Departamentos */}
          <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
            <h2 className="text-xl font-bold text-white mb-6 flex items-center gap-2">
              <BarChart3 className="w-5 h-5 text-blue-400" />
              Desempenho de Vendas (Últimos 30 Dias)
            </h2>
            <div className="h-[350px] w-full">
              <ResponsiveContainer width="100%" height="100%">
                <RechartsLineChart data={historicoGlobalArray || []}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
                  <XAxis
                    dataKey="data"
                    stroke="#64748b"
                    fontSize={11}
                    tickLine={false}
                    axisLine={false}
                    dy={10}
                  />
                  <YAxis
                    stroke="#64748b"
                    fontSize={11}
                    tickLine={false}
                    axisLine={false}
                    tickFormatter={(value: number) => `R$ ${value/1000}k`}
                  />
                  <Tooltip
                    contentStyle={{ backgroundColor: '#0f172a', border: '1px solid #334155', borderRadius: '12px', color: '#fff' }}
                    itemStyle={{ color: '#60a5fa' }}
                    cursor={{ stroke: '#334155', strokeWidth: 2 }}
                  />
                  <Legend iconType="circle" wrapperStyle={{ paddingTop: '20px' }} />
                  <Line
                    type="monotone"
                    dataKey="vendas"
                    name="Período Atual"
                    stroke="#3b82f6"
                    strokeWidth={4}
                    dot={{ fill: '#3b82f6', strokeWidth: 2, r: 4 }}
                    activeDot={{ r: 8, strokeWidth: 0 }}
                  />
                  <Line
                    type="monotone"
                    dataKey="meta"
                    name="Meta"
                    stroke="#94a3b8"
                    strokeWidth={2}
                    strokeDasharray="8 8"
                    dot={false}
                  />
                </RechartsLineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        {/* Right Column (1/3) */}
        <div className="space-y-6 md:space-y-8">
          {/* Top Vendedores */}
          {(user?.level === 'ti' || user?.level === 'admin' || user?.level === 'supervisor') && (
            <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
              <h2 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
                <Trophy className="w-5 h-5 text-yellow-400 fill-yellow-400/10" />
                Top Vendedores
              </h2>
              <div className="space-y-3">
                {topSellers.map((seller, index) => (
                  <div
                    key={seller.name}
                    className="group flex items-center gap-4 p-3 rounded-xl bg-slate-700/20 border border-slate-600/30 hover:border-blue-500/30 transition-all"
                  >
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-black ring-2 ring-offset-2 ring-offset-slate-800 ${
                        index === 0
                          ? 'bg-gradient-to-br from-yellow-300 to-yellow-600 text-black ring-yellow-500/50'
                          : index === 1
                            ? 'bg-gradient-to-br from-slate-200 to-slate-400 text-black ring-slate-400/50'
                            : 'bg-gradient-to-br from-orange-500 to-orange-800 text-white ring-orange-600/50'
                      }`}
                    >
                      {index + 1}
                    </div>
                    <div className="flex-1">
                      <div className="text-sm font-bold text-white group-hover:text-blue-400 transition-colors uppercase tracking-tight">{seller.name}</div>
                      <div className="text-xs text-slate-500 font-medium">{seller.sales} faturados</div>
                    </div>
                    <div className="flex flex-col items-end">
                      <span className="text-xs text-green-400 font-black">{seller.trend}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Atividades Recentes - Apenas Admin e TI */}
          {(user?.level === 'ti' || user?.level === 'admin') && (
            <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
              <h2 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
                <Activity className="w-5 h-5 text-blue-400" />
                Atividades Recentes
              </h2>
              <div className="space-y-6 relative">
                <div className="absolute left-1.5 top-2 bottom-2 w-px bg-slate-700" />
                {recentActivities.map((activity) => (
                  <div key={activity.id} className="relative pl-7">
                    <div
                      className={`absolute left-0 top-1.5 w-3 h-3 rounded-full border-2 border-slate-800 z-10 ${
                        activity.type === 'success'
                          ? 'bg-green-400 ring-4 ring-green-400/10'
                          : activity.type === 'warning'
                            ? 'bg-yellow-400 ring-4 ring-yellow-400/10'
                            : 'bg-blue-400 ring-4 ring-blue-400/10'
                      }`}
                    />
                    <div>
                      <p className="text-sm text-slate-200 font-medium leading-relaxed">{activity.text}</p>
                      <p className="text-[10px] text-slate-500 font-bold uppercase mt-0.5">{activity.time}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Weekly Summary mini graph */}
          <div className="rounded-2xl bg-slate-800 border border-slate-700 p-6 shadow-xl">
            <h2 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
              <Target className="w-5 h-5 text-cyan-400" />
              Resumo Semanal
            </h2>
            <div className="flex items-end gap-2 h-32">
              {['S', 'T', 'Q', 'Q', 'S', 'S'].map((day, i) => {
                const heights = [40, 65, 45, 80, 55, 30];
                return (
                  <div key={i} className="flex-1 flex flex-col items-center gap-2">
                    <div
                      className="w-full bg-slate-700 rounded-t-lg relative overflow-hidden group cursor-pointer"
                      style={{ height: '100%' }}
                    >
                      <div
                        className="absolute bottom-0 left-0 w-full bg-gradient-to-t from-blue-600 to-blue-400 rounded-t-lg transition-all duration-1000 group-hover:from-blue-400 group-hover:to-blue-300"
                        style={{ height: `${heights[i]}%` }}
                      />
                    </div>
                    <span className="text-[10px] font-black text-slate-500">{day}</span>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>

      {/* MODAL: Desempenho da Equipe (Supervisor) */}
      {showDesempenhoModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="bg-slate-800 border border-slate-700 rounded-2xl p-6 w-full max-w-2xl max-h-[80vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <LineChart className="w-5 h-5 text-blue-400" />
                Desempenho da Equipe (Hoje)
              </h3>
              <button onClick={() => setShowDesempenhoModal(false)} className="text-slate-400 hover:text-white">
                ✕
              </button>
            </div>
            <div className="space-y-4">
              {topSellers.map((seller, idx) => (
                <div key={idx} className="flex items-center justify-between p-4 bg-slate-700/50 rounded-xl border border-slate-600/50">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-slate-600 flex items-center justify-center text-sm font-bold text-white">
                      {idx + 1}
                    </div>
                    <div>
                      <div className="text-white font-medium">{seller.name}</div>
                      <div className="text-xs text-slate-400">Ativo</div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-white font-bold">{seller.sales}</div>
                    <div className="text-xs text-green-400">{seller.trend}</div>
                  </div>
                </div>
              ))}
              <p className="text-center text-sm text-slate-500 mt-4">Somente vendedores ativos da sua equipe.</p>
            </div>
          </div>
        </div>
      )}

      {/* MODAL: Metas da Equipe (Supervisor) */}
      {showMetasModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4">
          <div className="bg-slate-800 border border-slate-700 rounded-2xl p-6 w-full max-w-xl">
            <div className="flex justify-between items-center mb-6">
              <h3 className="text-xl font-bold text-white flex items-center gap-2">
                <Target className="w-5 h-5 text-purple-400" />
                Metas da Equipe
              </h3>
              <button onClick={() => setShowMetasModal(false)} className="text-slate-400 hover:text-white">
                ✕
              </button>
            </div>
            <div className="space-y-6">
              <div className="p-4 bg-slate-700/50 rounded-xl border border-slate-600/50 text-center">
                <div className="text-sm text-slate-400 mb-1">Vendas da Equipe (Hoje)</div>
                <div className="text-4xl font-black text-white">R$ 116.200</div>
              </div>
              <div>
                <div className="flex justify-between text-sm mb-2">
                  <span className="text-slate-300">Progresso da Meta Diária (R$ 150k)</span>
                  <span className="text-blue-400 font-bold">77%</span>
                </div>
                <div className="h-3 w-full bg-slate-700 rounded-full overflow-hidden">
                  <div className="h-full bg-gradient-to-r from-blue-500 to-cyan-400 w-[77%] rounded-full" />
                </div>
                <div className="text-right text-xs text-slate-500 mt-2">
                  Faltam R$ 33.800 para atingir a meta
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
