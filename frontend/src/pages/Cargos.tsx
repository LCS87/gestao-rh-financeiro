import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { Pencil, Trash2 } from 'lucide-react';
import { useState } from 'react';
import CrudLayout from '../components/CrudLayout';
import { api } from '../lib/api';
import { useAuth } from '../contexts/AuthContext';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { useConfirm } from '../components/ConfirmDialog';
import { SearchBar, Pagination, useSearchAndPagination } from '../components/SearchPagination';
import { useSortableTable, SortableTh } from '../components/SortableTable';
import type { Cargo } from '../types';

// Re-export for internal use

const CARGOS_PREDEFINIDOS = [
  'Supervisor',
  'Líder',
  'Coordenador',
  'Diretor',
  'TI',
  'Vendedor',
  'BKO',
  'Renovação',
  'Analista',
  'Gerente',
];

export default function Cargos() {
  const { user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState<Cargo | null>(null);
  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [showPredefined, setShowPredefined] = useState(false);
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const confirmAction = useConfirm();

  const { data: items = [], isLoading } = useQuery({
    queryKey: ['cargos'],
    queryFn: async () => {
      const { data } = await api.get<Cargo[]>('/rh/cargos');
      return data;
    },
  });

  const sp = useSearchAndPagination(items, ['nome', 'descricao'], 10);

  type CargoCol = 'nome' | 'descricao';
  const { sorted: sortedItems, sort, toggleSort } = useSortableTable<Cargo, CargoCol>(
    sp.paginated,
    (item, key) => item[key] ?? ''
  );

  function selectPredefinedCargo(cargoName: string) {
    setNome(cargoName);
    setShowPredefined(false);
    setEditing(null);
    setModalOpen(true);
  }

  const createMutation = useMutation({
    mutationFn: (body: { nome: string; descricao?: string }) => api.post('/rh/cargos', body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cargos'] });
      closeModal();
      showToast(editing ? 'Cargo atualizado!' : 'Cargo criado!', 'success');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: { nome: string; descricao?: string } }) =>
      api.put(`/rh/cargos/${id}`, body),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cargos'] });
      closeModal();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => api.delete(`/rh/cargos/${id}`),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['cargos'] });
      showToast('Cargo excluído.', 'success');
    },
  });

  function openModal(item?: Cargo) {
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

  if (isLoading) return <PageLoading text="Carregando cargos..." />;

  return (
    <CrudLayout title="Cargos" onAdd={() => openModal()} addText="Novo Cargo">
      <SearchBar value={sp.search} onChange={sp.setSearch} placeholder="Buscar cargo..." />
      {/* Botões de Cargos Predefinidos */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-sm font-medium text-slate-400">Cargos Predefinidos</h3>
          <button
            onClick={() => setShowPredefined(!showPredefined)}
            className="text-xs text-sky-400 hover:text-sky-300"
          >
            {showPredefined ? 'Ocultar' : 'Mostrar'}
          </button>
        </div>

        {showPredefined && (
          <div className="flex flex-wrap gap-2">
            {CARGOS_PREDEFINIDOS.map((cargo) => (
              <button
                key={cargo}
                onClick={() => selectPredefinedCargo(cargo)}
                className="px-3 py-1.5 bg-slate-700 text-slate-300 rounded-full text-sm hover:bg-sky-600 hover:text-white transition-colors"
              >
                + {cargo}
              </button>
            ))}
          </div>
        )}
      </div>

      <div className="rounded-xl border border-slate-700 overflow-hidden">
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
                <td className="px-4 py-3 text-white">{item.nome}</td>
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
                          title: 'Excluir cargo',
                          message: `Deseja excluir o cargo "${item.nome}"?`,
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
              {editing ? 'Editar' : 'Novo'} Cargo
            </h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label htmlFor="cargo-nome" className="block text-sm text-slate-400 mb-1">
                  Nome *
                </label>
                <input
                  id="cargo-nome"
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  className="w-full px-3 py-2 rounded-lg bg-slate-700 border border-slate-600 text-white"
                  required
                />
              </div>
              <div>
                <label htmlFor="cargo-descricao" className="block text-sm text-slate-400 mb-1">
                  Descrição
                </label>
                <textarea
                  id="cargo-descricao"
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
    </CrudLayout>
  );
}
