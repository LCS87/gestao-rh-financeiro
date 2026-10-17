const fs = require('fs');
const file = 'e:/ProjetoNovo/gestao-rh-financeiro/frontend/src/components/forms/EmployeeForm.tsx';

let d = fs.readFileSync(file, 'utf8');

const newSchema = `const employeeSchema = z.object({
  // 1. Informações Pessoais
  nome: z.string().min(3, 'Nome deve ter pelo menos 3 caracteres'),
  cpf: z.string().regex(/^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$/, 'CPF inválido'),
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
  cargo: z.string().optional(),
  departamento: z.string().optional(),
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
});`;

// Replace the schema definition
d = d.replace(/const employeeSchema = z\.object\(\{[\s\S]*?\}\);/, newSchema);

// Remove asterisks from input labels, except for Nome, CPF, Data de Nascimento
d = d.replace(/label="([^"]+?)\s\+"/g, (match, p1) => {
  if (['Nome Completo', 'CPF', 'Data de Nascimento'].includes(p1.trim())) {
    return `label="${p1} *"`;
  }
  return `label="${p1}"`;
});

// Since some use the asterisk escape character or raw asterisk 
d = d.replace(/label="([^"]+?)\s\*/g, (match, p1) => {
  if (['Nome Completo', 'CPF', 'Data de Nascimento'].includes(p1.trim())) {
    return `label="${p1} *`;
  }
  return `label="${p1}`;
});

fs.writeFileSync(file, d);
console.log('EmployeeForm.tsx updated successfully.');
