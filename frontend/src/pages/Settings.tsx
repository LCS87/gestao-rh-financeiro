import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '../lib/api';
import { useAuth } from '../contexts/AuthContext';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { Shield, UserX, UserCheck, Pencil, Plus, FileText, Settings as SettingsIcon } from 'lucide-react';

interface Usuario {
  id: string;
  username: string;
  role: string;
  enabled: boolean;
  createdAt: string;
}
interface ParametroSistema {
  chave: string;
  valor: string;
  descricao?: string;
}

interface LogAuditoria {
  id: string;
  usuarioId?: string;
  usuarioNome: string;
  acao: string;
  tabela: string;
  registroId?: string;
  detalhes?: string;
  ipAddress?: string;
  createdAt: string;
}

export default function Settings() {
  const { hasPermission } = useAuth();
  const [activeTab, setActiveTab] = useState('users');
  const queryClient = useQueryClient();
  const { showToast } = useToast();

  const [modalOpen, setModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<Usuario | null>(null);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    role: 'ROLE_USER',
    enabled: true,
  });

  const [systemParams, setSystemParams] = useState({
    theme: 'Dark Mode (Padrão)',
    globalGoal: '150000',
    allowLateSales: false,
  });

  const { data: auditoriaLogs, isLoading: isLoadingLogs } = useQuery({
    queryKey: ['auditoriaLogs'],
    queryFn: async () => {
      const { data } = await api.get<LogAuditoria[]>('/api/auditoria/recentes');
      return data;
    },
    enabled: hasPermission('ti'),
  });

  const { data: parametrosData } = useQuery({
    queryKey: ['parametrosSistema'],
    queryFn: async () => {
      const { data } = await api.get<ParametroSistema[]>('/api/parametros');
      return data;
    },
    enabled: hasPermission('ti'),
  });

  // Efeito para preencher o formulário inicial
  React.useEffect(() => {
    if (parametrosData) {
      const p = parametrosData.reduce((acc, curr) => ({ ...acc, [curr.chave]: curr.valor }), {} as any);
      setSystemParams({
        theme: p.theme || 'Dark Mode (Padrão)',
        globalGoal: p.META_VENDAS_DIARIA || '150000',
        allowLateSales: p.allowLateSales === 'true',
      });
    }
  }, [parametrosData]);

  const { data: users, isLoading } = useQuery({
    queryKey: ['usuarios'],
    queryFn: async () => {
      const { data } = await api.get<Usuario[]>('/api/usuarios');
      return data;
    },
    enabled: hasPermission('ti'),
  });

  const createMutation = useMutation({
    mutationFn: (data: any) => api.post('/api/usuarios', data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      showToast('Usuário criado com sucesso!', 'success');
      setModalOpen(false);
    },
    onError: () => showToast('Erro ao criar usuário.', 'error'),
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) => api.put(`/api/usuarios/${id}`, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      showToast('Usuário atualizado com sucesso!', 'success');
      setModalOpen(false);
    },
    onError: () => showToast('Erro ao atualizar usuário.', 'error'),
  });

  const toggleStatusMutation = useMutation({
    mutationFn: ({ id, enabled }: { id: string; enabled: boolean }) =>
      api.put(`/api/usuarios/${id}`, { enabled }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['usuarios'] });
      showToast('Status atualizado.', 'success');
    },
  });

  if (!hasPermission('ti')) {
    return (
      <div className="flex items-center justify-center p-12">
        <div className="text-center space-y-4">
          <Shield className="w-16 h-16 text-slate-600 mx-auto" />
          <h2 className="text-2xl font-bold text-white">Acesso Restrito</h2>
          <p className="text-slate-400">Esta área é exclusiva para a equipe de TI.</p>
        </div>
      </div>
    );
  }

  const openModal = (user?: Usuario) => {
    if (user) {
      setEditingUser(user);
      setFormData({
        username: user.username,
        password: '',
        role: user.role,
        enabled: user.enabled,
      });
    } else {
      setEditingUser(null);
      setFormData({
        username: '',
        password: '',
        role: 'ROLE_USER',
        enabled: true,
      });
    }
    setModalOpen(true);
  };

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingUser) {
      updateMutation.mutate({ id: editingUser.id, data: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  const saveParamsMutation = useMutation({
    mutationFn: async (data: Record<string, string>) => {
      // API nova pede array de objetos
      const payload = Object.entries(data).map(([chave, valor]) => ({
        chave,
        valor,
        descricao: `Parametro via UI: ${chave}`
      }));
      // Salva ou atualiza um a um para simplificar pro Java
      for (const param of payload) {
        await api.post('/api/parametros', param);
      }
      return true;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['parametrosSistema'] });
      showToast('Parâmetros salvos com sucesso!', 'success');
    },
    onError: () => showToast('Erro ao salvar parâmetros.', 'error'),
  });

  const saveParams = (e: React.FormEvent) => {
    e.preventDefault();
    saveParamsMutation.mutate({
      theme: systemParams.theme,
      META_VENDAS_DIARIA: systemParams.globalGoal,
      allowLateSales: systemParams.allowLateSales ? 'true' : 'false',
    });
  }

  const renderUsers = () => {
    if (isLoading) return <PageLoading text="Carregando usuários..." />;

    return (
      <div className="space-y-4">
        <div className="flex justify-end">
          <button onClick={() => openModal()} className="px-4 py-2 bg-purple-500 hover:bg-purple-600 text-white rounded-lg font-medium flex items-center gap-2">
            <Plus className="w-5 h-5"/> Novo Usuário
          </button>
        </div>
        <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-900 border-b border-slate-700">
              <tr>
                <th className="p-4 text-slate-400 font-medium">Usuário</th>
                <th className="p-4 text-slate-400 font-medium">Nível (Role)</th>
                <th className="p-4 text-slate-400 font-medium">Status</th>
                <th className="p-4 text-slate-400 font-medium">Criação</th>
                <th className="p-4 text-slate-400 font-medium text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              {users?.map((u) => (
                <tr key={u.id} className="border-b border-slate-700/50 hover:bg-slate-700/20">
                  <td className="p-4 text-white font-medium">{u.username}</td>
                  <td className="p-4">
                    <span className="px-2.5 py-1 rounded bg-slate-700 text-sky-400 text-xs font-semibold">
                      {u.role.replace('ROLE_', '')}
                    </span>
                  </td>
                  <td className="p-4">
                    <span
                      className={`px-2.5 py-1 rounded text-xs font-semibold ${
                        u.enabled ? 'bg-green-500/10 text-green-400' : 'bg-red-500/10 text-red-400'
                      }`}
                    >
                      {u.enabled ? 'Ativo' : 'Inativo'}
                    </span>
                  </td>
                  <td className="p-4 text-slate-400">
                    {new Date(u.createdAt).toLocaleDateString()}
                  </td>
                  <td className="p-4 flex gap-2 justify-end">
                    <button
                      onClick={() => toggleStatusMutation.mutate({ id: u.id, enabled: !u.enabled })}
                      className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-700 rounded transition-colors"
                      title={u.enabled ? 'Desativar usuário' : 'Ativar (Liberar) usuário'}
                    >
                      {u.enabled ? <UserX className="w-4 h-4" /> : <UserCheck className="w-4 h-4" />}
                    </button>
                    <button
                      onClick={() => openModal(u)}
                      className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-700 rounded transition-colors"
                      title="Promover Nível ou Resetar Senha"
                    >
                      <Pencil className="w-4 h-4" />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    );
  };

  const renderAuditLogs = () => {
    return (
      <div className="bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead className="bg-slate-900 border-b border-slate-700">
            <tr>
              <th className="p-4 text-slate-400 font-medium">Data/Hora</th>
              <th className="p-4 text-slate-400 font-medium">Usuário</th>
              <th className="p-4 text-slate-400 font-medium">Ação</th>
              <th className="p-4 text-slate-400 font-medium">Alvo</th>
              <th className="p-4 text-slate-400 font-medium">Status</th>
            </tr>
          </thead>
          <tbody>
            {auditoriaLogs?.map((log) => (
              <tr key={log.id} className="border-b border-slate-700/50 hover:bg-slate-700/20">
                <td className="p-4 text-slate-300 font-mono text-xs">{new Date(log.createdAt).toLocaleString()}</td>
                <td className="p-4 text-sky-400 font-medium">{log.usuarioNome}</td>
                <td className="p-4 text-white font-medium">{log.acao}</td>
                <td className="p-4 text-slate-400" title={log.tabela}>
                  {log.tabela.length > 50 ? `${log.tabela.substring(0, 50)}...` : log.tabela}
                </td>
                <td className="p-4">
                  <span className={`px-2 py-1 rounded text-[10px] font-bold ${log.acao.includes('DELETE') ? 'bg-red-500/10 text-red-400' : 'bg-green-500/10 text-green-400'}`} title={log.detalhes}>
                    SUCESSO
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {isLoadingLogs ? (
            <div className="p-4 text-center text-slate-400">Carregando logs...</div>
        ) : (
          <div className="p-4 border-t border-slate-700 text-xs text-slate-500 text-center">
            Exibindo os últimos 50 registros de auditoria capturados pelo backend AOP.
          </div>
        )}
      </div>
    );
  };

  const renderParams = () => {
    return (
      <form onSubmit={saveParams} className="bg-slate-800 rounded-xl border border-slate-700 p-6 max-w-2xl">
        <h3 className="text-lg font-bold text-white mb-6 flex items-center gap-2">
          <SettingsIcon className="w-5 h-5 text-purple-400" />
          Parâmetros Globais do Sistema
        </h3>
        <div className="space-y-4">
          <div>
            <label className="block text-sm text-slate-400 mb-1">Tema Padrão do Sistema</label>
            <select
              value={systemParams.theme}
              onChange={(e) => setSystemParams({...systemParams, theme: e.target.value})}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded text-white"
            >
              <option>Dark Mode (Padrão)</option>
              <option>Light Mode</option>
              <option>Sincronizar com Sistema</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-400 mb-1">Meta Global de Vendas (Diária) em R$</label>
            <input
              type="number"
              value={systemParams.globalGoal}
              onChange={(e) => setSystemParams({...systemParams, globalGoal: e.target.value})}
              className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded text-white"
            />
          </div>
          <div className="flex items-center gap-2 mt-4 pt-4 border-t border-slate-700">
            <input
              type="checkbox"
              id="allowLateStatus"
              checked={systemParams.allowLateSales}
              onChange={(e) => setSystemParams({...systemParams, allowLateSales: e.target.checked})}
              className="w-4 h-4 rounded border-slate-600 bg-slate-700 text-purple-500"
            />
            <label htmlFor="allowLateStatus" className="text-sm text-white">
              Permitir lançamento de vendas retroativas
            </label>
          </div>
          <div className="mt-8 flex justify-end">
            <button disabled={saveParamsMutation.isPending} type="submit" className="px-4 py-2 bg-purple-500 hover:bg-purple-600 text-white rounded font-medium disabled:opacity-50">
              {saveParamsMutation.isPending ? 'Salvando...' : 'Salvar Parâmetros'}
            </button>
          </div>
        </div>
      </form>
    );
  };


  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      <div>
        <h1 className="text-3xl font-bold text-white flex items-center gap-2">
          <Shield className="w-8 h-8 text-purple-400" />
          Configurações (TI)
        </h1>
        <p className="text-slate-400">Gerenciamento de acessos, auditoria e parâmetros do sistema.</p>
      </div>

      <div className="flex border-b border-slate-700 overflow-x-auto whitespace-nowrap">
        <button
          className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors flex items-center gap-2 ${
            activeTab === 'users' ? 'border-purple-400 text-purple-400' : 'border-transparent text-slate-400 hover:text-white'
          }`}
          onClick={() => setActiveTab('users')}
        >
          <Shield className="w-4 h-4" /> Gerenciamento de Usuários
        </button>
        <button
          className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors flex items-center gap-2 ${
            activeTab === 'audit' ? 'border-purple-400 text-purple-400' : 'border-transparent text-slate-400 hover:text-white'
          }`}
          onClick={() => setActiveTab('audit')}
        >
          <FileText className="w-4 h-4" /> Logs de Auditoria
        </button>
        <button
          className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors flex items-center gap-2 ${
            activeTab === 'params' ? 'border-purple-400 text-purple-400' : 'border-transparent text-slate-400 hover:text-white'
          }`}
          onClick={() => setActiveTab('params')}
        >
          <SettingsIcon className="w-4 h-4" /> Parâmetros do Sistema
        </button>
      </div>

      <div className="min-h-[400px]">
        {activeTab === 'users' && renderUsers()}
        {activeTab === 'audit' && renderAuditLogs()}
        {activeTab === 'params' && renderParams()}
      </div>

      {modalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4">
          <div className="bg-slate-800 rounded-xl p-6 max-w-md w-full border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-4">
              {editingUser ? (
                <>Editar Usuário: <span className="text-sky-400">{editingUser.username}</span></>
              ) : (
                'Novo Usuário'
              )}
            </h3>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="block text-sm text-slate-400 mb-1">Nome de Usuário</label>
                <input
                  type="text"
                  value={formData.username}
                  onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded text-white"
                  required
                />
              </div>
              <div>
                <label className="block text-sm text-slate-400 mb-1">
                  {editingUser ? 'Resetar Senha (Opcional)' : 'Senha Inicial'}
                </label>
                <div className="flex gap-2">
                  <input
                    type={editingUser ? "password" : "text"}
                    placeholder={editingUser ? "Deixe em branco para não alterar" : "Senha do usuário"}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded text-white"
                    required={!editingUser}
                  />
                  {editingUser && (
                    <button
                      type="button"
                      title="Forçar senha para admin123"
                      onClick={() => setFormData({ ...formData, password: 'admin123' })}
                      className="px-3 py-2 bg-slate-600 hover:bg-slate-500 rounded text-white text-xs font-semibold whitespace-nowrap"
                    >
                      Resetar admin123
                    </button>
                  )}
                </div>
              </div>
              <div>
                <label className="block text-sm text-slate-400 mb-1">Nível de Acesso (Role)</label>
                <select
                  value={formData.role}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                  className="w-full px-3 py-2 bg-slate-700 border border-slate-600 rounded text-white"
                >
                  <option value="ROLE_USER">USER (Vendedor)</option>
                  <option value="ROLE_SUPERVISOR">SUPERVISOR</option>
                  <option value="ROLE_ADMIN">ADMIN</option>
                  <option value="ROLE_TI">TI</option>
                </select>
              </div>
              <div className="flex items-center gap-2 mt-4 pt-4 border-t border-slate-700">
                <input
                  type="checkbox"
                  id="status"
                  checked={formData.enabled}
                  onChange={(e) => setFormData({ ...formData, enabled: e.target.checked })}
                  className="w-4 h-4 rounded border-slate-600 bg-slate-700 text-purple-500"
                />
                <label htmlFor="status" className="text-sm text-white">Acesso Liberado (Ativo)</label>
              </div>

              <div className="flex justify-end gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setModalOpen(false)}
                  className="px-4 py-2 rounded text-slate-400 hover:bg-slate-700"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={updateMutation.isPending || createMutation.isPending}
                  className="px-4 py-2 rounded bg-purple-500 hover:bg-purple-600 text-white font-medium disabled:opacity-50"
                >
                  {updateMutation.isPending || createMutation.isPending ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
