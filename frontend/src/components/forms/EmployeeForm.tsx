import { zodResolver } from '@hookform/resolvers/zod';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import { Input } from '../ui/Input';
import { Select } from '../ui/Select';
import { Button } from '../ui/button';

// Schema de validação completo
const employeeSchema = z.object({
  // 1. Informações Pessoais
  nome: z.string().min(3, 'Nome deve ter pelo menos 3 caracteres'),
  cpf: z.string().regex(/^\d{3}\.\d{3}\.\d{3}-\d{2}$/, 'CPF inválido'),
  rg: z.string().optional(),
  orgaoEmissor: z.string().optional(),
  dataNascimento: z.string().min(1, 'Data de nascimento é obrigatória'),
  estadoCivil: z.string().optional(),
  nacionalidade: z.string().optional(),
  naturalidade: z.string().optional(),
  nomeMae: z.string().optional(),
  nomePai: z.string().optional(),
  genero: z.string().optional(),
  deficiencia: z.string().optional(),

  // 2. Informações de Contato
  endereco: z.string().optional(),
  numero: z.string().optional(),
  complemento: z.string().optional(),
  bairro: z.string().optional(),
  cidade: z.string().optional(),
  estado: z.string().optional(),
  cep: z.string().optional(),
  telefoneCelular: z.string().optional(),
  telefoneFixo: z.string().optional(),
  emailPessoal: z.string().optional(),
  emailCorporativo: z.string().optional(),

  // 3. Documentação e Dados Trabalhistas
  pis: z.string().optional(),
  ctpsNumero: z.string().optional(),
  ctpsSerie: z.string().optional(),
  ctpsUf: z.string().optional(),
  tituloEleitor: z.string().optional(),
  tituloZona: z.string().optional(),
  tituloSecao: z.string().optional(),
  reservista: z.string().optional(),
  cnh: z.string().optional(),
  cnhCategoria: z.string().optional(),
  escolaridade: z.string().optional(),
  profissao: z.string().optional(),

  // 4. Dados Bancários
  banco: z.string().optional(),
  agencia: z.string().optional(),
  conta: z.string().optional(),
  tipoConta: z.string().optional(),
  chavePix: z.string().optional(),

  // 5. Dados do Contrato
  dataAdmissao: z.string().optional(),
  cargo: z.string().min(1, 'Cargo é obrigatório'),
  departamento: z.string().min(1, 'Departamento é obrigatório'),
  salario: z.string().optional(),
  tipoContrato: z.string().optional(),
  horarioTrabalho: z.string().optional(),
  centroCusto: z.string().optional(),
  supervisor: z.string().optional(),

  // 7. Contato de Emergência
  emergenciaNome: z.string().optional(),
  emergenciaParentesco: z.string().optional(),
  emergenciaTelefone: z.string().optional(),

  // 8. Dados para Benefícios
  tamanhoUniforme: z.string().optional(),
  planoSaude: z.string().optional(),
  restricaoAlimentar: z.string().optional(),

  status: z.string().optional(),
});

type EmployeeFormData = z.infer<typeof employeeSchema>;

interface EmployeeFormProps {
  initialData?: Partial<EmployeeFormData>;
  onSubmit: (data: EmployeeFormData) => void;
  isLoading?: boolean;
  onCancel?: () => void;
}

const ESTADOS_CIVIS = ['Solteiro', 'Casado', 'Divorciado', 'Viúvo', 'União Estável'];
const GENEROS = ['Masculino', 'Feminino', 'Outro', 'Prefiro não informar'];
const ESCOLARIDADES = [
  'Fundamental',
  'Médio',
  'Técnico',
  'Superior',
  'Pós-graduação',
  'Mestrado',
  'Doutorado',
];
const TIPOS_CONTRATO = ['CLT', 'Estágio', 'PJ', 'Temporário', 'Experiência'];
const TIPOS_CONTA = ['Corrente', 'Poupança', 'Conta Salário'];
const DEPARTAMENTOS = ['Comercial', 'RH e Financeiro', 'Renovação', 'BKO', 'TI'];
const UNIFORMES = ['PP', 'P', 'M', 'G', 'GG', 'XG', 'XXG'];
const CARGOS = ['BKO', 'Vendedor', 'Renovação', 'Supervisor', 'Coordenador', 'TI', 'Diretor'];

export function EmployeeForm({
  initialData,
  onSubmit,
  isLoading = false,
  onCancel,
}: EmployeeFormProps) {
  const [activeTab, setActiveTab] = useState('pessoal');

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EmployeeFormData>({
    resolver: zodResolver(employeeSchema),
    defaultValues: initialData,
  });

  const handleFormSubmit = (data: EmployeeFormData) => {
    // Converte strings vazias em `undefined` para evitar erro HTTP 400 de Parsing no Java (Data e Numbers)
    const cleanedData = Object.fromEntries(
      Object.entries(data).map(([key, value]) => [key, value === "" ? undefined : value])
    );
    onSubmit(cleanedData as EmployeeFormData);
  };

  const handleInvalid = (validationErrors: any) => {
    console.error("Erros de validação silenciosos:", validationErrors);
    alert("Não foi possível enviar: há " + Object.keys(validationErrors).length + " campo(s) obrigatório(s) pendente(s) ou inválidos. Verifique as mensagens em vermelho abaixo dos campos.");
  };

  const tabs = [
    { id: 'pessoal', label: 'Pessoal', icon: '👤' },
    { id: 'contato', label: 'Contato', icon: '📞' },
    { id: 'documentos', label: 'Documentos', icon: '📄' },
    { id: 'bancario', label: 'Bancário', icon: '🏦' },
    { id: 'contrato', label: 'Contrato', icon: '💼' },
    { id: 'emergencia', label: 'Emergência', icon: '🚨' },
  ];

  return (
    <form onSubmit={handleSubmit(handleFormSubmit, handleInvalid)} className="space-y-6">
      {/* Abas de navegação */}
      <div className="flex flex-wrap gap-2 border-b border-slate-700 pb-4">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${activeTab === tab.id
                ? 'bg-sky-500 text-white'
                : 'bg-slate-700 text-slate-300 hover:bg-slate-600'
              }`}
          >
            {tab.icon} {tab.label}
          </button>
        ))}
      </div>

      {/* Conteúdo das abas */}
      <div className="space-y-6">
        {/* Aba 1: Informações Pessoais */}
        {activeTab === 'pessoal' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">👤 Informações Pessoais</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Nome Completo *"
                {...register('nome')}
                error={errors.nome?.message}
                placeholder="Digite o nome completo"
              />
              <Input
                label="CPF *"
                {...register('cpf')}
                error={errors.cpf?.message}
                placeholder="000.000.000-00"
              />
              <Input
                label="RG"
                {...register('rg')}
                error={errors.rg?.message}
                placeholder="00.000.000-0"
              />
              <Input
                label="Órgão Emissor"
                {...register('orgaoEmissor')}
                error={errors.orgaoEmissor?.message}
                placeholder="SSP/SP"
              />
              <Input
                label="Data de Nascimento *"
                type="date"
                {...register('dataNascimento')}
                error={errors.dataNascimento?.message}
              />
              <Select
                label="Estado Civil"
                {...register('estadoCivil')}
                error={errors.estadoCivil?.message}
              >
                <option value="">Selecione</option>
                {ESTADOS_CIVIS.map((ec) => (
                  <option key={ec} value={ec}>
                    {ec}
                  </option>
                ))}
              </Select>
              <Input
                label="Nacionalidade"
                {...register('nacionalidade')}
                error={errors.nacionalidade?.message}
                placeholder="Brasileiro"
              />
              <Input
                label="Naturalidade"
                {...register('naturalidade')}
                error={errors.naturalidade?.message}
                placeholder="São Paulo/SP"
              />
              <Input
                label="Nome da Mãe"
                {...register('nomeMae')}
                error={errors.nomeMae?.message}
                placeholder="Nome completo da mãe"
              />
              <Input
                label="Nome do Pai"
                {...register('nomePai')}
                placeholder="Nome completo do pai (opcional)"
              />
              <div>
                <Select label="Gênero" {...register('genero')} error={errors.genero?.message}>
                  <option value="">Selecione</option>
                  {GENEROS.map((g) => (
                    <option key={g} value={g}>
                      {g}
                    </option>
                  ))}
                </Select>
                <Input
                  label="Deficiência (PCD)"
                  {...register('deficiencia')}
                  placeholder="Tipo de deficiência (se aplicável)"
                />
              </div>
              <Select
                label="Departamento *"
                {...register('departamento')}
                error={errors.departamento?.message}
              >
                <option value="">Selecione um departamento</option>
                {DEPARTAMENTOS.map((d) => (
                  <option key={d} value={d}>
                    {d}
                  </option>
                ))}
              </Select>
              <Select
                label="Cargo *"
                {...register('cargo')}
                error={errors.cargo?.message}
              >
                <option value="">Selecione um cargo</option>
                {CARGOS.map((c) => (
                  <option key={c} value={c}>
                    {c}
                  </option>
                ))}
              </Select>
            </div>
          </div>
        )}

        {/* Aba 2: Informações de Contato */}
        {activeTab === 'contato' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">📞 Informações de Contato</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Endereço"
                {...register('endereco')}
                error={errors.endereco?.message}
                placeholder="Rua, Avenida, etc."
              />
              <Input
                label="Número"
                {...register('numero')}
                error={errors.numero?.message}
                placeholder="123"
              />
              <Input
                label="Complemento"
                {...register('complemento')}
                placeholder="Apto 45, Bloco B"
              />
              <Input
                label="Bairro"
                {...register('bairro')}
                error={errors.bairro?.message}
                placeholder="Centro"
              />
              <Input
                label="Cidade"
                {...register('cidade')}
                error={errors.cidade?.message}
                placeholder="São Paulo"
              />
              <Input
                label="Estado"
                {...register('estado')}
                error={errors.estado?.message}
                placeholder="SP"
                maxLength={2}
              />
              <Input
                label="CEP"
                {...register('cep')}
                error={errors.cep?.message}
                placeholder="00000-000"
              />
              <Input
                label="Telefone Celular"
                {...register('telefoneCelular')}
                error={errors.telefoneCelular?.message}
                placeholder="(00) 00000-0000"
              />
              <Input
                label="Telefone Fixo"
                {...register('telefoneFixo')}
                placeholder="(00) 0000-0000"
              />
              <Input
                label="Email Pessoal"
                type="email"
                {...register('emailPessoal')}
                error={errors.emailPessoal?.message}
                placeholder="email@pessoal.com"
              />
              <Input
                label="Email Corporativo"
                type="email"
                {...register('emailCorporativo')}
                placeholder="email@empresa.com"
              />
            </div>
          </div>
        )}

        {/* Aba 3: Documentação e Dados Trabalhistas */}
        {activeTab === 'documentos' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">
              📄 Documentação e Dados Trabalhistas
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="PIS/PASEP"
                {...register('pis')}
                error={errors.pis?.message}
                placeholder="000.00000.00-0"
              />
              <Input
                label="CTPS Número"
                {...register('ctpsNumero')}
                error={errors.ctpsNumero?.message}
                placeholder="00000"
              />
              <Input
                label="CTPS Série"
                {...register('ctpsSerie')}
                error={errors.ctpsSerie?.message}
                placeholder="00000"
              />
              <Input
                label="CTPS UF"
                {...register('ctpsUf')}
                error={errors.ctpsUf?.message}
                placeholder="SP"
                maxLength={2}
              />
              <Input
                label="Título de Eleitor"
                {...register('tituloEleitor')}
                placeholder="0000 0000 0000"
              />
              <Input label="Zona Eleitoral" {...register('tituloZona')} placeholder="000" />
              <Input label="Seção Eleitoral" {...register('tituloSecao')} placeholder="0000" />
              <Input
                label="Reservista"
                {...register('reservista')}
                placeholder="Número da reservista"
              />
              <Input label="CNH" {...register('cnh')} placeholder="00000000000" />
              <Input
                label="Categoria CNH"
                {...register('cnhCategoria')}
                placeholder="A, B, C, D, E, AB, etc."
              />
              <Select
                label="Escolaridade"
                {...register('escolaridade')}
                error={errors.escolaridade?.message}
              >
                <option value="">Selecione</option>
                {ESCOLARIDADES.map((e) => (
                  <option key={e} value={e}>
                    {e}
                  </option>
                ))}
              </Select>
              <Input
                label="Profissão"
                {...register('profissao')}
                error={errors.profissao?.message}
                placeholder="Analista, Vendedor, etc."
              />
            </div>
          </div>
        )}

        {/* Aba 4: Dados Bancários */}
        {activeTab === 'bancario' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">🏦 Dados Bancários</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Banco"
                {...register('banco')}
                error={errors.banco?.message}
                placeholder="Nome do banco"
              />
              <Input
                label="Agência"
                {...register('agencia')}
                error={errors.agencia?.message}
                placeholder="0000-0"
              />
              <Input
                label="Conta"
                {...register('conta')}
                error={errors.conta?.message}
                placeholder="00000-0"
              />
              <Select
                label="Tipo de Conta"
                {...register('tipoConta')}
                error={errors.tipoConta?.message}
              >
                <option value="">Selecione</option>
                {TIPOS_CONTA.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </Select>
              <Input
                label="Chave PIX"
                {...register('chavePix')}
                placeholder="CPF, Email ou Celular"
              />
            </div>
          </div>
        )}

        {/* Aba 5: Dados do Contrato */}
        {activeTab === 'contrato' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">
              💼 Dados do Contrato de Trabalho
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Data de Admissão"
                type="date"
                {...register('dataAdmissao')}
                error={errors.dataAdmissao?.message}
              />
            </div>
            <Input
              label="Salário Bruto"
              type="number"
              {...register('salario')}
              error={errors.salario?.message}
              placeholder="0.00"
              step="0.01"
            />
            <Select
              label="Tipo de Contrato"
              {...register('tipoContrato')}
              error={errors.tipoContrato?.message}
            >
              <option value="">Selecione</option>
              {TIPOS_CONTRATO.map((t) => (
                <option key={t} value={t}>
                  {t}
                </option>
              ))}
            </Select>
            <Input
              label="Horário de Trabalho"
              {...register('horarioTrabalho')}
              error={errors.horarioTrabalho?.message}
              placeholder="08h às 18h"
            />
            <Input
              label="Centro de Custo / Filial"
              {...register('centroCusto')}
              placeholder="Matriz, Filial 1, etc."
            />
            <Input
              label="Supervisor / Gestor Imediato"
              {...register('supervisor')}
              placeholder="Nome do supervisor"
            />
            <Select label="Status" {...register('status')}>
              <option value="ATIVO">Ativo</option>
              <option value="INATIVO">Inativo</option>
              <option value="FERIAS">Férias</option>
              <option value="AFASTADO">Afastado</option>
            </Select>
            <Select label="Tamanho do Uniforme" {...register('tamanhoUniforme')}>
              <option value="">Selecione</option>
              {UNIFORMES.map((u) => (
                <option key={u} value={u}>
                  {u}
                </option>
              ))}
            </Select>
            <Input
              label="Plano de Saúde/Odonto"
              {...register('planoSaude')}
              placeholder="Operadora e carteirinha"
            />
            <Input
              label="Restrição Alimentar"
              {...register('restricaoAlimentar')}
              placeholder="Alergias, restrições, etc."
            />
          </div>
        )}

        {/* Aba 6: Contato de Emergência */}
        {activeTab === 'emergencia' && (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white mb-4">🚨 Contato de Emergência</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Input
                label="Nome Completo *"
                {...register('emergenciaNome')}
                error={errors.emergenciaNome?.message}
                placeholder="Nome do contato de emergência"
              />
              <Input
                label="Grau de Parentesco"
                {...register('emergenciaParentesco')}
                error={errors.emergenciaParentesco?.message}
                placeholder="Cônjuge, Pai, Mãe, Irmão, etc."
              />
              <Input
                label="Telefone para Contato"
                {...register('emergenciaTelefone')}
                error={errors.emergenciaTelefone?.message}
                placeholder="(00) 00000-0000"
              />
            </div>
          </div>
        )}
      </div>

      {/* Ações */}
      <div className="flex gap-4 pt-6 border-t border-slate-700">
        {onCancel && (
          <Button type="button" variant="secondary" onClick={onCancel} disabled={isLoading}>
            Cancelar
          </Button>
        )}

        <Button type="submit" variant="default" disabled={isLoading}>
          {isLoading ? 'Processando...' : initialData ? 'Atualizar' : 'Cadastrar'} Funcionário
        </Button>
      </div>
    </form>
  );
}
