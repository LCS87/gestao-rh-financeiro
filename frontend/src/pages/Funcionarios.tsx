import React from 'react';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/card';
import { useEmployees } from '../hooks/useApi';
import { useAuth } from '../contexts/AuthContext';
import { EmployeeForm } from '../components/forms/EmployeeForm';
import { Button } from '../components/ui/button';
import { useCreateEmployee, useUpdateEmployee, useDeleteEmployee } from '../hooks/useApi';
import { X, Users, User, Phone, FileText, Landmark, Briefcase, AlertTriangle, Gift, ClipboardList } from 'lucide-react';
import { PageLoading } from '../components/Layout';
import { useToast } from '../components/Toast';
import { useConfirm } from '../components/ConfirmDialog';
import { SearchBar, Pagination, useSearchAndPagination } from '../components/SearchPagination';
import { AccessibleModal } from '../components/AccessibleModal';
import type { Funcionario, FuncionarioForm } from '../types';

export function FuncionariosPage() {
  const { user, hasPermission } = useAuth();
  const { data: employees, isLoading, error } = useEmployees();
  const createEmployee = useCreateEmployee();
  const updateEmployee = useUpdateEmployee();
  const deleteEmployee = useDeleteEmployee();
  const { showToast } = useToast();
  const confirm = useConfirm();
  const [showForm, setShowForm] = React.useState(false);
  const [editingEmployee, setEditingEmployee] = React.useState<Funcionario | null>(null);
  const [selectedEmployee, setSelectedEmployee] = React.useState<Funcionario | null>(null);
  const [showDetailModal, setShowDetailModal] = React.useState(false);

  const sp = useSearchAndPagination(employees || [], ['nome', 'cargo', 'departamento'], 10);

  if (!hasPermission('admin')) {
    return (
      <div className="text-white text-center mt-10">
        <h2 className="text-2xl font-bold mb-4">Acesso Negado</h2>
        <p className="text-slate-400">Você não tem permissão para acessar esta página.</p>
      </div>
    );
  }

  if (isLoading) {
    return <PageLoading text="Carregando funcionários..." />;
  }

  if (error) {
    return (
      <div className="text-red-400 text-center mt-10">
        <p>Erro ao carregar funcionários: {error.message}</p>
      </div>
    );
  }

  const handleSubmit = async (data: FuncionarioForm) => {
    try {
      if (editingEmployee) {
        await updateEmployee.mutateAsync({ id: editingEmployee.id, ...data });
        showToast('Funcionário atualizado com sucesso!', 'success');
      } else {
        await createEmployee.mutateAsync(data);
        showToast('Funcionário cadastrado com sucesso!', 'success');
      }
      setShowForm(false);
      setEditingEmployee(null);
    } catch (error) {
      console.error('Erro ao salvar funcionário:', error);
      showToast('Erro ao salvar funcionário.', 'error');
    }
  };

  const handleEdit = (employee: Funcionario) => {
    setEditingEmployee(employee);
    setShowForm(true);
  };

  const handleDelete = async (id: string) => {
    const confirmed = await confirm({
      title: 'Excluir funcionário',
      message: 'Tem certeza que deseja excluir este funcionário? Esta ação não pode ser desfeita.',
      confirmText: 'Excluir',
      variant: 'danger',
    });
    if (confirmed) {
      try {
        await deleteEmployee.mutateAsync(id);
        showToast('Funcionário excluído.', 'success');
      } catch (error) {
        console.error('Erro ao excluir funcionário:', error);
        showToast('Erro ao excluir funcionário.', 'error');
      }
    }
  };

  const handleViewDetail = (employee: Funcionario) => {
    setSelectedEmployee(employee);
    setShowDetailModal(true);
  };

  const handleCloseDetail = () => {
    setShowDetailModal(false);
    setSelectedEmployee(null);
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingEmployee(null);
  };

  const DetailItem = ({ label, value }: { label: string; value: React.ReactNode }) => (
    <div className="flex flex-col">
      <span className="text-xs text-slate-400 uppercase tracking-wide">{label}</span>
      <span className="text-white font-medium">
        {value || <span className="text-slate-500">-</span>}
      </span>
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-white flex items-center gap-2"><Users className="w-8 h-8 text-blue-400" /> Funcionários</h1>
          <p className="text-slate-300">Gerenciamento de funcionários</p>
        </div>
        <Button onClick={() => setShowForm(true)}>Novo Funcionário</Button>
      </div>

      {showForm && (
        <Card>
          <CardHeader>
            <CardTitle>{editingEmployee ? 'Editar Funcionário' : 'Novo Funcionário'}</CardTitle>
          </CardHeader>
          <CardContent>
            <EmployeeForm
              initialData={editingEmployee as Record<string, unknown> | undefined}
              onSubmit={handleSubmit}
              isLoading={createEmployee.isPending || updateEmployee.isPending}
              onCancel={handleCancel}
            />
          </CardContent>
        </Card>
      )}

      <SearchBar
        value={sp.search}
        onChange={sp.setSearch}
        placeholder="Buscar por nome, cargo ou departamento..."
      />

      <div className="grid gap-4">
        {sp.paginated.map((employee) => (
          <Card key={employee.id}>
            <CardContent className="p-6">
              <div className="flex justify-between items-start">
                <div className="space-y-2">
                  <button
                    onClick={() => handleViewDetail(employee)}
                    className="text-lg font-semibold text-white hover:text-sky-400 hover:underline cursor-pointer text-left"
                  >
                    {employee.nome}
                  </button>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="text-slate-400">Cargo:</span>
                      <span className="text-white ml-2">{employee.cargo}</span>
                    </div>
                    <div>
                      <span className="text-slate-400">Departamento:</span>
                      <span className="text-white ml-2">{employee.departamento}</span>
                    </div>
                    <div>
                      <span className="text-slate-400">Status:</span>
                      <span
                        className={`ml-2 ${employee.status === 'Ativo' ? 'text-green-400' : 'text-red-400'}`}
                      >
                        {employee.status}
                      </span>
                    </div>
                    {hasPermission('admin') && (
                      <div>
                        <span className="text-slate-400">Salário:</span>
                        <span className="text-white ml-2">
                          {employee.salario ? `R$ ${employee.salario}` : '---'}
                        </span>
                      </div>
                    )}
                  </div>
                </div>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" onClick={() => handleEdit(employee)}>
                    Editar
                  </Button>
                  {user?.level === 'ti' && (
                    <Button variant="destructive" size="sm" onClick={() => handleDelete(employee.id)}>
                      Excluir
                    </Button>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <Pagination
        currentPage={sp.currentPage}
        totalPages={sp.totalPages}
        totalItems={sp.totalItems}
        pageSize={sp.pageSize}
        onPageChange={sp.setCurrentPage}
      />

      {/* Modal de Detalhes do Funcionário */}
      <AccessibleModal
        open={showDetailModal && !!selectedEmployee}
        onClose={handleCloseDetail}
        ariaLabel={`Detalhes de ${selectedEmployee?.nome || 'funcionário'}`}
      >
        {selectedEmployee && (
          <div className="bg-slate-800 rounded-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-slate-700">
            {/* Header */}
            <div className="flex justify-between items-center p-6 border-b border-slate-700">
              <h2 className="text-xl font-bold text-white flex items-center gap-2"><User className="w-5 h-5 text-blue-400" /> {selectedEmployee.nome}</h2>
              <button
                onClick={handleCloseDetail}
                className="p-2 text-slate-400 hover:text-white hover:bg-slate-700 rounded-lg transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Conteúdo */}
            <div className="p-6 space-y-6">
              {/* Informações Pessoais */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <ClipboardList className="w-5 h-5" /> Informações Pessoais
                </h3>
                <DetailItem label="CPF" value={selectedEmployee.cpf} />
                <DetailItem label="RG" value={selectedEmployee.rg} />
                <DetailItem label="Órgão Emissor" value={selectedEmployee.orgaoEmissor} />
                <DetailItem label="Data de Nascimento" value={selectedEmployee.dataNascimento} />
                <DetailItem label="Estado Civil" value={selectedEmployee.estadoCivil} />
                <DetailItem label="Gênero" value={selectedEmployee.genero} />
                <DetailItem label="Nacionalidade" value={selectedEmployee.nacionalidade} />
                <DetailItem label="Naturalidade" value={selectedEmployee.naturalidade} />
                <DetailItem label="Nome da Mãe" value={selectedEmployee.nomeMae} />
                <DetailItem label="Nome do Pai" value={selectedEmployee.nomePai} />
                <DetailItem label="Deficiência (PCD)" value={selectedEmployee.deficiencia} />
              </div>

              {/* Contato */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <Phone className="w-5 h-5" /> Contato
                </h3>
                <DetailItem
                  label="Endereço"
                  value={`${selectedEmployee.endereco || ''}, ${selectedEmployee.numero || ''}`}
                />
                <DetailItem label="Bairro" value={selectedEmployee.bairro} />
                <DetailItem
                  label="Cidade/Estado"
                  value={`${selectedEmployee.cidade || ''}/${selectedEmployee.estado || ''}`}
                />
                <DetailItem label="CEP" value={selectedEmployee.cep} />
                <DetailItem label="Celular" value={selectedEmployee.telefoneCelular} />
                <DetailItem label="Telefone Fixo" value={selectedEmployee.telefoneFixo} />
                <DetailItem label="Email Pessoal" value={selectedEmployee.emailPessoal} />
                <DetailItem label="Email Corporativo" value={selectedEmployee.emailCorporativo} />
              </div>

              {/* Documentos */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <FileText className="w-5 h-5" /> Documentos
                </h3>
                <DetailItem label="PIS/PASEP" value={selectedEmployee.pis} />
                <DetailItem
                  label="CTPS"
                  value={`${selectedEmployee.ctpsNumero || ''} - ${selectedEmployee.ctpsSerie || ''} (${selectedEmployee.ctpsUf || ''})`}
                />
                <DetailItem label="Título de Eleitor" value={selectedEmployee.tituloEleitor} />
                <DetailItem label="Reservista" value={selectedEmployee.reservista} />
                <DetailItem
                  label="CNH"
                  value={`${selectedEmployee.cnh || ''} ${selectedEmployee.cnhCategoria || ''}`}
                />
                <DetailItem label="Escolaridade" value={selectedEmployee.escolaridade} />
                <DetailItem label="Profissão" value={selectedEmployee.profissao} />
              </div>

              {/* Dados Bancários */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <Landmark className="w-5 h-5" /> Dados Bancários
                </h3>
                <DetailItem label="Banco" value={selectedEmployee.banco} />
                <DetailItem label="Agência" value={selectedEmployee.agencia} />
                <DetailItem label="Conta" value={selectedEmployee.conta} />
                <DetailItem label="Tipo de Conta" value={selectedEmployee.tipoConta} />
                <DetailItem label="Chave PIX" value={selectedEmployee.chavePix} />
              </div>

              {/* Contrato */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <Briefcase className="w-5 h-5" /> Dados do Contrato
                </h3>
                <DetailItem label="Cargo" value={selectedEmployee.cargo} />
                <DetailItem label="Departamento" value={selectedEmployee.departamento} />
                <DetailItem label="Data de Admissão" value={selectedEmployee.dataAdmissao} />
                <DetailItem label="Tipo de Contrato" value={selectedEmployee.tipoContrato} />
                <DetailItem
                  label="Salário"
                  value={selectedEmployee.salario ? `R$ ${selectedEmployee.salario}` : null}
                />
                <DetailItem label="Horário" value={selectedEmployee.horarioTrabalho} />
                <DetailItem label="Supervisor" value={selectedEmployee.supervisor} />
                <DetailItem label="Status" value={selectedEmployee.status} />
              </div>

              {/* Emergência */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <AlertTriangle className="w-5 h-5" /> Contato de Emergência
                </h3>
                <DetailItem label="Nome" value={selectedEmployee.emergenciaNome} />
                <DetailItem label="Parentesco" value={selectedEmployee.emergenciaParentesco} />
                <DetailItem label="Telefone" value={selectedEmployee.emergenciaTelefone} />
              </div>

              {/* Benefícios */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 border-t border-slate-700 pt-4">
                <h3 className="col-span-full text-lg font-semibold text-sky-400 mb-2 flex items-center gap-2">
                  <Gift className="w-5 h-5" /> Benefícios
                </h3>
                <DetailItem label="Tamanho Uniforme" value={selectedEmployee.tamanhoUniforme} />
                <DetailItem label="Plano de Saúde" value={selectedEmployee.planoSaude} />
                <DetailItem
                  label="Restrição Alimentar"
                  value={selectedEmployee.restricaoAlimentar}
                />
              </div>
            </div>

            {/* Footer */}
            <div className="flex justify-end gap-3 p-6 border-t border-slate-700">
              <Button variant="outline" onClick={handleCloseDetail}>
                Fechar
              </Button>
              <Button
                onClick={() => {
                  handleCloseDetail();
                  handleEdit(selectedEmployee);
                }}
              >
                Editar Funcionário
              </Button>
            </div>
          </div>
        )}
      </AccessibleModal>
    </div>
  );
}
