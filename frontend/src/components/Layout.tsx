import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import {
  LayoutDashboard,
  Users,
  Building2,
  Briefcase,
  DollarSign,
  Settings,
  LogOut,
  Menu,
  X,
  Wrench,
  Crown,
  User,
  Trophy,
  Wallet,
  FileText,
  Palmtree,
} from 'lucide-react';

// Reusable loading spinner component
export function LoadingSpinner({ size = 'md', text }: { size?: 'sm' | 'md' | 'lg'; text?: string }) {
  const sizeClasses = {
    sm: 'w-5 h-5 border-2',
    md: 'w-8 h-8 border-[3px]',
    lg: 'w-12 h-12 border-4',
  };

  return (
    <div className="flex flex-col items-center justify-center gap-3">
      <div
        className={`${sizeClasses[size]} rounded-full border-slate-600 border-t-blue-400 animate-spin`}
      />
      {text && <span className="text-sm text-slate-400">{text}</span>}
    </div>
  );
}

// Full-page loading state
export function PageLoading({ text = 'Carregando...' }: { text?: string }) {
  return (
    <div className="flex items-center justify-center h-64">
      <LoadingSpinner size="lg" text={text} />
    </div>
  );
}

// Nav item type for sidebar
interface NavItem {
  to: string;
  icon: React.ReactNode;
  label: string;
  requiredLevel?: 'ti' | 'admin' | 'supervisor' | 'user' | 'financeiro' | 'rh';
}

// Level badge component
function LevelBadge({ level }: { level: string }) {
  const config = {
    ti: { icon: Wrench, label: 'TI', bg: 'bg-purple-500/20', text: 'text-purple-400', desc: '(Acesso Total)' },
    admin: { icon: Crown, label: 'Admin', bg: 'bg-blue-500/20', text: 'text-blue-400', desc: '(Gestão)' },
    supervisor: { icon: Trophy, label: 'Superv', bg: 'bg-orange-500/20', text: 'text-orange-400', desc: '(Vendas/Dash)' },
    user: { icon: User, label: 'User', bg: 'bg-green-500/20', text: 'text-green-400', desc: '(Vendas)' },
  }[level] || { icon: User, label: level, bg: 'bg-slate-500/20', text: 'text-slate-400', desc: '' };

  const Icon = config.icon;

  return (
    <div className="flex items-center gap-2 bg-slate-700 rounded-lg px-3 py-1.5">
      <span className="text-xs text-slate-400">Nível:</span>
      <span className={`text-xs font-semibold px-2 py-0.5 rounded flex items-center gap-1 ${config.bg} ${config.text}`}>
        <Icon className="w-3 h-3" />
        {config.label}
      </span>
      <span className="text-xs text-slate-500">{config.desc}</span>
    </div>
  );
}

export default function Layout({ children }: { children: React.ReactNode }) {
  const { user, logout, hasPermission } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const navItems: NavItem[] = [
    {
      to: '/dashboard',
      icon: <LayoutDashboard className="w-5 h-5" />,
      label: 'Dashboard',
    },
    {
      to: '/employees',
      icon: <Users className="w-5 h-5" />,
      label: 'Funcionários',
      requiredLevel: 'admin',
    },
    {
      to: '/departamentos',
      icon: <Building2 className="w-5 h-5" />,
      label: 'Departamentos',
      requiredLevel: 'admin',
    },
    {
      to: '/cargos',
      icon: <Briefcase className="w-5 h-5" />,
      label: 'Cargos',
      requiredLevel: 'admin',
    },
    {
      to: '/fluxo-caixa',
      icon: <Wallet className="w-5 h-5 text-emerald-400" />,
      label: 'Fluxo de Caixa',
      requiredLevel: 'financeiro',
    },
    {
      to: '/holerites',
      icon: <FileText className="w-5 h-5 text-blue-400" />,
      label: 'Folha Pagamento',
      requiredLevel: 'rh',
    },
    {
      to: '/ferias',
      icon: <Palmtree className="w-5 h-5 text-orange-400" />,
      label: 'Férias (RH)',
      requiredLevel: 'rh',
    },
    {
      to: '/sales',
      icon: <DollarSign className="w-5 h-5" />,
      label: 'Vendas',
      requiredLevel: 'user',
    },
    {
      to: '/settings',
      icon: <Settings className="w-5 h-5" />,
      label: 'Configurações',
      requiredLevel: 'ti',
    },
  ];

  // Filter nav items based on user permissions
  const visibleNavItems = navItems.filter(
    (item) => !item.requiredLevel || hasPermission(item.requiredLevel)
  );

  const sidebarContent = (
    <>
      <div className="mb-8">
        <div className="flex items-center gap-2">
          <Building2 className="w-6 h-6 text-blue-400" />
          <h2 className="text-xl font-bold text-white">Menu</h2>
        </div>
        <p className="text-slate-400 text-sm mt-1">Bem-vindo, {user?.name}</p>
      </div>

      <nav className="space-y-1 flex-1">
        {visibleNavItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            onClick={() => setSidebarOpen(false)}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium transition-all duration-200 ${
                isActive
                  ? 'bg-blue-500/15 text-blue-400 border border-blue-500/30'
                  : 'text-slate-300 hover:bg-slate-700 hover:text-white border border-transparent'
              }`
            }
          >
            {item.icon}
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* Logout button at bottom */}
      <button
        onClick={logout}
        className="flex items-center gap-3 px-4 py-2.5 rounded-lg text-sm font-medium text-red-400 hover:bg-red-500/10 hover:text-red-300 transition-all duration-200 mt-4 border border-transparent hover:border-red-500/20"
      >
        <LogOut className="w-5 h-5" />
        Sair
      </button>
    </>
  );

  return (
    <div className="min-h-screen bg-slate-900 flex">
      {/* Mobile overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 bg-black/60 z-40 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar — Desktop: always visible; Mobile: slide-in */}
      <div
        className={`fixed lg:static inset-y-0 left-0 z-50 w-64 bg-slate-800 border-r border-slate-700 p-4 flex flex-col transform transition-transform duration-300 ease-in-out ${
          sidebarOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'
        }`}
      >
        {/* Mobile close button */}
        <button
          onClick={() => setSidebarOpen(false)}
          className="absolute top-4 right-4 p-1.5 rounded-lg text-slate-400 hover:text-white hover:bg-slate-700 lg:hidden"
        >
          <X className="w-5 h-5" />
        </button>

        {sidebarContent}
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Header */}
        <header className="bg-slate-800 border-b border-slate-700 px-4 md:px-6 py-4 flex justify-between items-center">
          <div className="flex items-center gap-3">
            {/* Hamburger — only on mobile */}
            <button
              onClick={() => setSidebarOpen(true)}
              className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-700 lg:hidden"
            >
              <Menu className="w-5 h-5" />
            </button>

            <div className="flex items-center gap-2">
              <Building2 className="w-5 h-5 text-blue-400 hidden sm:block" />
              <h1 className="text-lg md:text-xl font-bold text-white">Gestão RH-Financeiro</h1>
            </div>

            <div className="hidden md:flex">
              <LevelBadge level={user?.level || 'user'} />
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 p-4 md:p-6 overflow-y-auto">{children}</main>
      </div>
    </div>
  );
}
