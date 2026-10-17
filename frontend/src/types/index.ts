// ── Funcionário ──────────────────────────────────

export interface Funcionario {
  id: string;
  nome: string;
  cpf?: string;
  rg?: string;
  orgaoEmissor?: string;
  dataNascimento?: string;
  estadoCivil?: string;
  genero?: string;
  nacionalidade?: string;
  naturalidade?: string;
  nomeMae?: string;
  nomePai?: string;
  deficiencia?: string;
  email?: string;
  emailPessoal?: string;
  emailCorporativo?: string;
  telefone?: string;
  telefoneCelular?: string;
  telefoneFixo?: string;
  endereco?: string;
  numero?: string;
  bairro?: string;
  cidade?: string;
  estado?: string;
  cep?: string;
  cargo?: string;
  departamento?: string;
  status?: string;
  salario?: number;
  salarioBase?: number;
  dataAdmissao?: string;
  dataDemissao?: string;
  banco?: string;
  agencia?: string;
  conta?: string;
  tipoConta?: string;
  pix?: string;
  chavePix?: string;
  pis?: string;
  ctpsNumero?: string;
  ctpsSerie?: string;
  ctpsUf?: string;
  tituloEleitor?: string;
  reservista?: string;
  cnh?: string;
  cnhCategoria?: string;
  escolaridade?: string;
  profissao?: string;
  tipoContrato?: string;
  horarioTrabalho?: string;
  supervisor?: string;
  contatoEmergenciaNome?: string;
  contatoEmergenciaTelefone?: string;
  contatoEmergenciaParentesco?: string;
  emergenciaNome?: string;
  emergenciaParentesco?: string;
  emergenciaTelefone?: string;
  beneficios?: string;
  preferenciaAlimentar?: string;
  restricaoAlimentar?: string;
  tamanhoUniforme?: string;
  planoSaude?: string;
  departamentoId?: string;
  cargoId?: string;
  equipeId?: string;
  escalaId?: string;
  [key: string]: unknown;
}

export type FuncionarioForm = Omit<Funcionario, 'id'>;

// ── Departamento ─────────────────────────────────

export interface Departamento {
  id: string;
  nome: string;
  descricao?: string;
}

// ── Cargo ────────────────────────────────────────

export interface Cargo {
  id: string;
  nome: string;
  descricao?: string;
}

// ── Vendas ───────────────────────────────────────

export interface Vendedor {
  id: string; // Adaptado para receber UUID do Funcionario backend
  nome: string;
  metaDiaria: number;
  vendasDiarias: number;
  vendasSemanais: number;
  vendasMensais: number;
  status: string;
}

export interface SalePayload {
  sellerId: string;
  amount: number;
}

// ── Dashboard ────────────────────────────────────

export interface DashboardIndicators {
  totalFuncionarios?: number;
  vendasMes?: number;
  turnover?: number;
  absenteismo?: number;
  [key: string]: unknown;
}

// ── Financeiro (Contas e Folha) ────────────────────

export interface ContaPagar {
  id: string;
  descricao: string;
  fornecedor: string;
  valorTotal: number;
  valorPago: number;
  dataVencimento: string;
  dataPagamento?: string;
  status: 'PENDENTE' | 'PARCIAL' | 'PAGO' | 'CANCELADO';
  categoria: string;
}

export interface ContaReceber {
  id: string;
  descricao: string;
  cliente: string;
  valorTotal: number;
  valorRecebido: number;
  dataVencimento: string;
  dataRecebimento?: string;
  status: 'PENDENTE' | 'PARCIAL' | 'PAGO' | 'CANCELADO';
  categoria: string;
}

export interface FolhaPagamento {
  id: string;
  funcionarioOrigemId?: string;
  anoMes: string;
  diasUteisBase: number;
  diasTrabalhados: number;
  salarioBase: number;
  totalProventos: number;
  totalDescontos: number;
  salarioLiquido: number;
  status: 'PROCESSANDO' | 'FECHADA' | 'PAGA' | 'CANCELADA';
  itens?: any[];
}
