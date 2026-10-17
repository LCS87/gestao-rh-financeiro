import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Pencil, Trash2, Users } from 'lucide-react';
import { useState } from 'react';
import CrudLayout from '../components/CrudLayout';
import { api } from '../lib/api';
import { useAuth } from '../contexts/AuthContext';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { useConfirm } from '../components/ConfirmDialog';
import { SearchBar, Pagination, useSearchAndPagination } from '../components/SearchPagination';
import { useSortableTable, SortableTh } from '../components/SortableTable';
import type { Departamento } from '../types';

// Re-export for internal use

interface Funcionario {
  id: string;
  nome: string;
  cargo: string;
  status: string;
}

const MOCK_FUNCIONARIOS: Record<string, Funcionario[]> = {
  Comercial: [
    { id: '1', nome: 'João Silva', cargo: 'Vendedor', status: 'Ativo' },
    { id: '2', nome: 'Maria Santos', cargo: 'Supervisor', status: 'Ativo' },
    { id: '3', nome: 'Pedro Costa', cargo: 'Vendedor', status: 'Ativo' },
  ],
  'RH e Financeiro': [
    { id: '4', nome: 'Ana Oliveira', cargo: 'Analista RH', status: 'Ativo' },
    { id: '5', nome: 'Carlos Souza', cargo: 'Gestor Financeiro', status: 'Ativo' },
  ],
  Renovação: [
    { id: '6', nome: 'Fernanda Lima', cargo: 'Consultor', status: 'Ativo' },
    { id: '7', nome: 'Ricardo Mendes', cargo: 'Líder', status: 'Ativo' },
  ],
  BKO: [
    { id: '8', nome: 'Juliana Torres', cargo: 'Analista', status: 'Ativo' },
    { id: '9', nome: 'Marcos Pereira', cargo: 'Coordenador', status: 'Ativo' },
  ],
  TI: [
    { id: '10', nome: 'Gabriel Tech', cargo: 'Desenvolvedor', status: 'Ativo' },
    { id: '11', nome: 'Lucia Dev', cargo: 'Tech Lead', status: 'Ativo' },
  ],
};

export default function Departamentos() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Departamento | null>(null);
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [employeeModalOpen, setEmployeeModalOpen] = useState(false);
  const [selectedDepartment, setSelectedDepartment] = useState<string | null>(null);
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const confirmAction = useConfirm();

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['departamentos'],
    queryFn: async () => {
      const { data } = await api.get<Departamento[]>('/rh/departamentos');
      return data;
    },
  });

  const sp = useSearchAndPagination(items, ['nome', 'descricao'], 10);

  type DeptCol = 'nome' | 'descricao';
  const { sorted: sortedItems, sort, toggleSort } = useSortableTable<Departamento, DeptCol>(
    sp.paginated,
    (item, key) => item[key] ?? ''
  );

  const handleShowEmployees = (deptName: string) => {
    setSelectedDepartment(deptName);
    setEmployeeModalOpen(true);
  };

  const createMutation = useMutation({
    mutationFn: (body: { nome: string; descricao?: string }) => api.post('/rh/departamentos', body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['departamentos'] });
      closeModal();
      showToast(editing ? 'Departamento atualizado!' : 'Departamento criado!', 'success');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: { nome: string; descricao?: string } }) =>
      api.put(`/rh/departamentos/${id}`, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['departamentos'] });
      closeModal();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/rh/departamentos/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['departamentos'] });
      showToast('Departamento excluído.', 'success');
    },
  });

  function openModal(item?: Departamento) {
    if (item) {
      setEditing(item);
      setNome(item.nome);
      setDescricao(item.descricao ?? '');
    } else {
      setEditing(null);
      setNome('');
      setDescricao('');
    }
    setModalOpen(true);
  }

  function closeModal() {
    setModalOpen(false);
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    const body = { nome, descricao: descricao || undefined };
    if (editing) updateMutation.mutate({ id: editing.id, body });
    else createMutation.mutate(body);
  }

  if (isLoading) return <PageLoading text="Carregando departamentos..." />;

  return (
    <CrudLayout title="Departamentos" onAdd={() => openModal()} addText="Novo Departamento">
      <SearchBar value={sp.search} onChange={sp.setSearch} placeholder="Buscar departamento..." />
      <div className="rounded-xl border border-slate-700 overflow-hidden mt-4">
        <table className="w-full text-left">
          <thead className="bg-slate-800">
            <tr>
              <SortableTh column="nome" label="Nome" currentSort={sort} onSort={toggleSort} />
              <SortableTh column="descricao" label="Descrição" currentSort={sort} onSort={toggleSort} />
              <th className="px-4 py-3 text-slate-400 font-medium w-24">Ações</th>
            </tr>
          </thead>
          <tbody>
            {sortedItems.map((item) => (
              <tr key={item.id} className="border-t border-slate-700 hover:bg-slate-800/50">
                <td className="px-4 py-3 text-white">
                  <button
                    onClick={() => handleShowEmployees(item.nome)}
                    className="flex items-center gap-2 text-sky-400 hover:text-sky-300 hover:underline"
                  >
                    <Users className="w-4 h-4" />
                    {item.nome}
                  </button>
                </td>
                <td className="px-4 py-3 text-slate-400">{item.descricao ?? '-'}</td>
                <td className="px-4 py-3 flex gap-2">
                  <button
                    onClick={() => openModal(item)}
                    className="p-2 rounded text-slate-400 hover:bg-slate-700 hover:text-white"
                  >
                    <Pencil className="w-4 h-4" />
                  </button>
                  {user?.level === 'ti' && (
                    <button
                      onClick={async () => {
                        const ok = await confirmAction({
                          title: 'Excluir departamento',
                          message: `Deseja excluir o departamento "${item.nome}"?`,
                          confirmText: 'Excluir',
                          variant: 'danger',
                        });
                        if (ok) deleteMutation.mutate(item.id);
                      }}
                      className="p-2 rounded text-slate-400 hover:bg-red-500/20 hover:text-red-400"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Pagination
        currentPage={sp.currentPage}
        totalPages={sp.totalPages}
        totalItems={sp.totalItems}
        pageSize={sp.pageSize}
        onPageChange={sp.setCurrentPage}
      />

      {modalOpen && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4">
          <div className="bg-slate-800 rounded-xl max-w-md w-full p-6 border border-slate-700">
            <h2 className="text-lg font-semibold text-white mb-4">
              {editing ? 'Editar' : 'Novo'} Departamento
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label htmlFor="departamento-nome" className="block text-sm text-slate-400 mb-1">
                  Nome *
                </label>
                <input
                  id="departamento-nome"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg bg-slate-700 border border-slate-600 text-white"
                  required
                />
              </div>
              <div>
                <label
                  htmlFor="departamento-descricao"
                  className="block text-sm text-slate-400 mb-1"
                >
                  Descrição
                </label>
                <textarea
                  id="departamento-descricao"
                  value={descricao}
                  onChange={(e) => setDescricao(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg bg-slate-700 border border-slate-600 text-white"
                  rows={3}
                />
              </div>
              <div className="flex justify-end gap-2 mt-6">
                <button
                  type="button"
                  onClick={closeModal}
                  className="px-4 py-2 rounded-lg border border-slate-600 text-slate-400"
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-sky-500 text-white hover:bg-sky-600"
                >
                  {editing ? 'Salvar' : 'Criar'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Modal de Funcionários por Departamento */}
      {employeeModalOpen && selectedDepartment && (
        <div className="fixed inset-0 bg-black/60 flex items-center justify-center z-50 p-4">
          <div className="bg-slate-800 rounded-xl max-w-lg w-full p-6 border border-slate-700 max-h-[80vh] overflow-y-auto">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-lg font-semibold text-white flex items-center gap-2">
                <Users className="w-5 h-5 text-blue-400" /> {selectedDepartment} - Funcionários
              </h2>
              <button
                onClick={() => setEmployeeModalOpen(false)}
                className="text-slate-400 hover:text-white"
              >
                ✕
              </button>
            </div>

            <div className="space-y-3">
              {MOCK_FUNCIONARIOS[selectedDepartment]?.map((func) => (
                <div
                  key={func.id}
                  className="flex items-center justify-between p-3 bg-slate-700 rounded-lg"
                >
                  <div>
                    <div className="font-medium text-white">{func.nome}</div>
                    <div className="text-sm text-slate-400">{func.cargo}</div>
                  </div>
                  <span
                    className={`px-2 py-1 rounded text-xs ${func.status === 'Ativo'
                        ? 'bg-green-500/20 text-green-400'
                        : 'bg-red-500/20 text-red-400'
                      }`}
                  >
                    {func.status}
                  </span>
                </div>
              ))}
            </div>

            <div className="mt-4 pt-4 border-t border-slate-700">
              <div className="text-sm text-slate-400">
                Total: {MOCK_FUNCIONARIOS[selectedDepartment]?.length || 0} funcionários
              </div>
            </div>
          </div>
        </div>
      )}
    </CrudLayout>
  );
}
