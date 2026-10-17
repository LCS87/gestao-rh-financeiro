# Sistema de Gestão de RH e Financeiro para Call Center

Sistema integrado de gestão de Recursos Humanos e Financeiro desenvolvido para empresas de call center com alta rotatividade, operando em ambiente on-premise.

## 📋 Visão Geral

Este sistema foi projetado para resolver os principais desafios de gestão de RH e financeiro em call centers:

- ✅ Alta rotatividade de funcionários (admissões e demissões constantes)
- ✅ Controle de documentos admissionais e exames periódicos
- ✅ Gestão de escalas rotativas (6x1, 12x36, plantões)
- ✅ Cálculo de comissões complexas por vendas
- ✅ Folha de pagamento com variáveis e descontos
- ✅ Fluxo de caixa integrado com provisão de folha
- ✅ Operação em servidor local sem dependência de nuvem

## 🏗️ Arquitetura

### Estrutura Modular

O sistema segue uma arquitetura **Monolito Modular** com comunicação assíncrona:

```
gestao-rh-financeiro/
├── core/                      # Módulo base com configurações comuns
├── modulo-rh/                  # Módulo de Recursos Humanos
├── modulo-financeiro/          # Módulo Financeiro
├── modulo-comissoes/           # Módulo de Comissões
├── modulo-documentos/          # Módulo de Gestão Documental
├── modulo-dashboard/           # Módulo de Relatórios e Dashboards
└── modulo-notificacoes/        # Módulo de Notificações Internas
```

Cada módulo segue a estrutura **Clean Architecture**:

- **API (Controller)**: Interfaces REST, validação de entrada
- **Application (Service)**: Orquestração, casos de uso, transações
- **Domain**: Entidades, Value Objects, regras de negócio
- **Infrastructure**: Repositórios, integrações, acesso a dados

## 🛠️ Tecnologias

### Backend
- **Java 17 LTS**
- **Spring Boot 3.1.x**
- **Spring Security + JWT**
- **Spring Data JPA + Hibernate**
- **PostgreSQL 15**
- **Redis 7** (Cache)
- **RabbitMQ 3.12** (Comunicação assíncrona)
- **Flyway** (Migrações de banco)
- **JasperReports** (Relatórios PDF)
- **SpringDoc OpenAPI** (Documentação API)

### Frontend
- **React 19** + **TypeScript**
- **Vite 7** (build)
- **React Router** (navegação)
- **TanStack Query** (dados)
- **Tailwind CSS** (estilos)
- **Axios** (API)
- **Lucide React** (ícones)

### Infraestrutura
- **Docker & Docker Compose** (Desenvolvimento)
- **Nginx** (Proxy reverso - produção)
- **Prometheus + Grafana** (Monitoramento)

## 🚀 Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- Docker e Docker Compose
- Git

### Passo a Passo

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd gestao-rh-financeiro
```

2. **Inicie os serviços com Docker Compose**
```bash
docker-compose up -d
```

Isso irá iniciar:
- PostgreSQL (porta 5432)
- Redis (porta 6379)
- RabbitMQ (porta 5672) + Management UI (porta 15672)

3. **Configure as variáveis de ambiente** (opcional)

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis:

```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
JWT_SECRET=your-secret-key-minimum-32-characters
JASYPT_PASSWORD=your-jasypt-password
SPRING_PROFILES_ACTIVE=dev
```

4. **Execute as migrações do banco**

As migrações são executadas automaticamente via Flyway na inicialização:
- `V1__create_base_tables.sql`
- `V2__create_usuarios_table.sql`
- `V3__create_admissao_demissao_documentos_tables.sql`
- `V4__fix_salario_base_column_type.sql`
- `V5__create_escalas_ponto_ferias_afastamentos.sql`
- `V7__fix_folha_pagamento_ano_mes_column.sql`

5. **Execute o backend**

```bash
mvn spring-boot:run
```

Ou compile e execute:

```bash
mvn clean package
java -jar target/gestao-rh-financeiro-1.0.0-SNAPSHOT.jar
```

> **Nota:** Se a porta 8080 estiver em uso, execute com outra porta:
> ```powershell
> $env:SERVER_PORT="8081"; mvn spring-boot:run
> ```
> Nesse caso, ajuste o proxy no `frontend/vite.config.ts` para `target: 'http://localhost:8081'`.

6. **Execute o frontend**

```bash
cd frontend
npm install
npm run dev
```

### ✅ Qualidade de código (frontend)

O frontend inclui lint (ESLint) e formatação (Prettier):

```bash
cd frontend
npm run lint
npm run lint:fix
npm run format
```

7. **Acesse a aplicação**

#### 🏠 Acesso Local (Rede Interna)
- **Interface web**: http://localhost:3000
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health**: http://localhost:8080/actuator/health
- **RabbitMQ**: http://localhost:15672 (guest/guest)

#### 📱 Acesso Remoto (Mesma Rede Wi-Fi)
- **Dispositivos móveis**: http://192.168.0.5:3004
- **Outros computadores**: http://192.168.0.5:3004
- **Smart TVs**: http://192.168.0.5:3004

#### 🌐 Acesso Externo (Qualquer Lugar)
Para acessar o sistema de outra rede, use uma das opções abaixo:

**Opção 1: NGROK (Recomendado - 5 minutos)**
```bash
# 1. Criar conta em https://dashboard.ngrok.com/signup
# 2. Obter authtoken em https://dashboard.ngrok.com/get-started/your-authtoken
# 3. Configurar:
cd frontend
.\ngrok.exe config add-authtoken SEU_TOKEN
# 4. Iniciar túnel:
.\ngrok.exe http 3004
# 5. Usar URL gerada: https://xxxxx.ngrok.io
```

**Opção 2: VPN Corporativa**
- Configurar servidor VPN no Windows
- Conectar-se remotamente via VPN
- Acessar: http://localhost:3004

**Opção 3: Port Forwarding**
- Configurar roteador: Porta Externa 3004 → IP Interno 192.168.0.5:3004
- Acessar: http://187.19.172.104:3004

> **Importante**: O sistema está configurado com `host: '0.0.0.0'` para permitir acesso externo. Use NGROK para acesso rápido e seguro.

O frontend faz proxy de `/api` para o backend (porta configurada no `vite.config.ts`).

8. **Login padrão (ambiente de desenvolvimento)**

- **Usuário**: `admin`
- **Senha**: `admin123`

Um usuário ADMIN é criado automaticamente pelo `DataInitializer` na primeira execução.

## 📚 Documentação da API

Após iniciar o backend, acesse o Swagger UI:

http://localhost:8080/swagger-ui.html

### Principais Endpoints

#### Módulo RH
- `GET /rh/funcionarios` - Listar funcionários
- `POST /rh/funcionarios` - Criar funcionário
- `GET /rh/escalas` - Listar escalas de trabalho
- `GET /rh/ponto/funcionario/{id}/calcular-horas` - Calcular horas trabalhadas
- `GET /rh/dashboard/indicadores` - Indicadores de RH

#### Módulo Financeiro
- `GET /financeiro/contas-pagar` - Listar contas a pagar
- `POST /financeiro/contas-pagar` - Criar conta a pagar
- `GET /financeiro/folha-pagamento/mes/{anoMes}` - Listar folhas do mês
- `POST /financeiro/folha-pagamento/funcionario/{id}/mes/{anoMes}` - Gerar folha
- `GET /financeiro/contracheques/{id}/pdf` - Baixar contracheque
- `POST /financeiro/agendamento-folha/gerar/{anoMes}` - Gerar folhas mensais

## 🧪 Testes

Execute os testes com:

```bash
mvn test
```

Para verificar a cobertura de testes:

```bash
mvn jacoco:report
```

O relatório estará disponível em: `target/site/jacoco/index.html`

## 📦 Estrutura do Projeto

```
gestao-rh-financeiro/
├── frontend/                    # Interface React (login, CRUD, dashboards)
├── src/
│   ├── main/
│   │   ├── java/br/com/callcenter/
│   │   │   ├── core/                    # Configurações comuns, segurança, JWT
│   │   │   ├── modulorh/                # Módulo RH (funcionários, admissão, demissão, documentos)
│   │   │   ├── modulo-financeiro/       # Módulo Financeiro (contas, folha, contracheques)
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.yml          # Configurações principais
│   │       ├── db/migration/            # Scripts Flyway
│   │       └── reports/                 # Templates JasperReports
│   └── test/                            # Testes
├── docker/                              # Scripts Docker
├── docker-compose.yml                   # Orquestração de serviços
└── pom.xml                              # Dependências Maven
```

## 🗺️ Roadmap

### Fase 1: Fundação e Módulo RH Básico (Semanas 1-8) ✅
- ✅ Setup do projeto e configuração do ambiente
- ✅ Autenticação e autorização (JWT + roles)
- ✅ CRUD de funcionários, departamentos, cargos e equipes
- ✅ Módulo de admissão e demissão (fluxos e cálculo básico de rescisão)
- ✅ Gestão de documentos (upload/download, validade, alertas a vencer/vencidos)
- ✅ **Frontend React** (login, dashboard, CRUDs, layout responsivo)

### Fase 2: Controle de Ponto e Escala (Semanas 9-16) ✅
- ✅ Modelagem de escalas de trabalho (6x1, 12x36, comercial, plantões)
- ✅ CRUD de escalas e vinculação a funcionários
- ✅ Entidade RegistroPonto (entrada, saída, intervalo)
- ✅ API para bater ponto e listar registros por período
- ✅ Service para totalizar horas normais e extras
- ✅ Regras CLT (44h semanais, 50% extra)
- ✅ Entidades: Ferias, Afastamento
- ✅ Fluxo de solicitação e aprovação
- ✅ Dashboard de RH (headcount, turnover, documentos vencendo)
- ✅ Gráficos e cards no frontend

### Fase 3: Módulo Financeiro e Folha (Semanas 17-24) ✅
- ✅ Entidades ContaPagar e ContaReceber
- ✅ CRUD de contas com status e vencimentos
- ✅ Cálculo da folha de pagamento (CLT completa)
- ✅ Cálculo automático de INSS, IRRF, FGTS
- ✅ Gestão de proventos e descontos variáveis
- ✅ Geração de contracheques em PDF (JasperReports)
- ✅ Agendamento automático da folha (@Scheduled)
- ✅ Processamento: CRIAÇÃO → PROCESSAMENTO → PAGAMENTO
- ✅ Validação de duplicatas por funcionário/mês

### Fase 4: Comissões, Vendas e Dashboards Reais (Semanas 25-32) ✅
- ✅ Entidade e Módulo Vendas (Back-end base)
- ✅ Integração de Dados Reais nos Gráficos (Dashboard e Tela Vendas)
- ✅ Endpoints analíticos (Resumo Mensal e Ranking Diário)
- ✅ Configuração de Metas (Ex: Meta Diária de 1400)
- ✅ Substituição dos Mocks Recharts para `TanStack Query`

### Fase 5: Módulo de Auditoria, Configurações e Implantação (Semanas 33-36) ✅
- ✅ Entidade e Tabela `ParametroSistema` (Chaves Dinâmicas)
- ✅ Injeção Dinâmica da `META_VENDAS_DIARIA` em Tela de Vendas
- ✅ Injeção de Interceptador AOP (`@Audited`) nas Rotas de Delete e Criar
- ✅ Painel de **Settings.tsx** (Usuários, Auditoria de Logs Seguros, e Parâmetros Front-End)
- ✅ Testes com usuários reais finalizados
- ✅ Deploy finalizado e Build Validado no React+Vite

## 🔒 Segurança

- Autenticação via JWT com refresh token
- Autorização baseada em roles (RBAC)
- Criptografia de dados sensíveis (Jasypt)
- Auditoria de operações críticas
- Validação de entrada em todas as APIs

## 📊 Requisitos Não-Funcionais

- **Performance**: APIs respondem em < 500ms (95% dos casos)
- **Concorrência**: Suporta até 50 usuários simultâneos por módulo
- **Disponibilidade**: 99% uptime (horário comercial)
- **Backup**: Diário com retenção de 30 dias

## 👥 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
2. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
3. Push para a branch (`git push origin feature/nova-feature`)
4. Abra um Pull Request

## 📝 Licença

Este projeto é proprietário e confidencial.

## 📧 Contato

Para dúvidas ou suporte, entre em contato com a equipe de desenvolvimento.

## 🔧 Recursos Implementados

### Módulo RH ✅
- **Funcionários**: CRUD completo com 45+ campos de cadastro
  - 📋 Informações Pessoais: Nome, CPF, RG, Data Nascimento, Estado Civil, Gênero, Deficiência
  - 📞 Contato: Email, Telefones, Endereço Completo (Rua, Número, Bairro, Cidade, Estado, CEP)
  - 📄 Documentação: PIS, CTPS, Título Eleitor, Reservista, CNH, Escolaridade
  - 💳 Dados Bancários: Banco, Agência, Conta, Tipo Conta, Chave PIX
  - 📋 Contrato: Data Admissão, Cargo, Departamento, Salário, Tipo Contrato, Horário, Supervisor
  - 🚨 Contato Emergência: Nome, Parentesco, Telefone
  - ✏️ **Edição Completa**: Editar todos os dados do funcionário
  - 👁️ **Modal de Detalhes**: Clique no nome para visualizar informações completas
  - 🗑️ **Exclusão**: Remover funcionários com confirmação
- **Departamentos/Cargos**: Estrutura organizacional com CRUD completo
- **Admissão/Demissão**: Fluxos completos com cálculos rescisórios
- **Documentos**: Upload, download, controle de validade com CRUD
- **Escalas**: Configuração 6x1, 12x36, comercial, plantões com CRUD
- **Ponto**: Registro de entrada/saída, cálculo de horas extras
- **Férias**: Solicitação, aprovação, controle de períodos
- **Afastamentos**: Tipos variados, controle de períodos
- **Dashboard**: Indicadores em tempo real com navegação completa

### Módulo Financeiro ✅
- **Contas a Pagar**: Gestão completa com status e vencimentos
- **Contas a Receber**: Controle de recebíveis e carteira
- **Folha de Pagamento**: Cálculo CLT (INSS, IRRF, FGTS)
- **Contracheques**: Geração em PDF com JasperReports
- **Comissões**: Sistema completo com gamificação e rankings
- **Dashboard Financeiro**: Widgets em tempo real e KPIs
- **Relatórios Gerenciais**: Análises e exportações

### Módulo Testes ✅
- **Testes com Usuários**: Validação com perfis diferentes
- **Documentação**: Guias, manuais e vídeos tutoriais
- **Deploy**: Sistema completo de implantação
- **Checklist**: Validação de qualidade e performance

### Frontend Moderno ✅
- **React 19 + TypeScript**: Interface moderna e type-safe
- **Shadcn/UI + Tailwind**: Design system profissional
- **Navegação Completa**: Sidebar funcional com routing
- **Dashboards Inteligentes**: Widgets em tempo real
- **Gamificação**: Rankings e conquistas para comissões
- **Escalas Interativas**: Gantt Chart com drag-and-drop
- **Pipeline Kanban**: Processo de admissão digital
- **Simulador de Folha**: Cálculos em tempo real
- **Sistema de Testes**: Validação completa com usuários
- **Modais Interativos**: Popup de detalhes dos funcionários
- **Formulários Avançados**: 45+ campos com validação
- **Interface Responsiva**: Funciona em todos os dispositivos
- **Acesso Multi-dispositivo**: Local, remoto e externo

### Funcionalidades Técnicas ✅
- **Autenticação**: JWT com refresh token
- **Autorização**: RBAC com roles (ADMIN, RH, FINANCEIRO, etc.)
- **Segurança**: Jasypt para dados sensíveis
- **Cache**: Redis para performance
- **Mensageria**: RabbitMQ para processos assíncronos
- **Agendamento**: Tarefas automáticas com @Scheduled
- **Relatórios**: JasperReports para PDFs
- **Documentação**: OpenAPI/Swagger integrado

---

## 🌐 Acesso ao Sistema

### 📋 Resumo de Acesso

| Tipo | URL | Dispositivos | Requisitos |
|------|-----|--------------|------------|
| 🏠 **Local** | http://localhost:3000 | PC Principal | Sistema instalado |
| 📱 **Remoto** | http://192.168.0.5:3000 | Mesma rede Wi-Fi | Conectado à mesma rede |
| 🌐 **Externo** | https://connotative-unaccustomedly-deadra.ngrok-free.dev | Qualquer lugar | NGROK configurado ✅ |

### 🚀 Configuração Rápida (NGROK)

```bash
# 1. Criar conta: https://dashboard.ngrok.com/signup
# 2. Obter token: https://dashboard.ngrok.com/get-started/your-authtoken
# 3. Configurar:
cd frontend
.\ngrok.exe config add-authtoken SEU_TOKEN
# 4. Iniciar túnel:
.\ngrok.exe http 3000
# 5. Usar URL: https://connotative-unaccustomedly-deadra.ngrok-free.dev
```

### 📱 Funcionalidades Disponíveis

- ✅ **Cadastro Completo**: 45+ campos para funcionários
- ✅ **Edição e Exclusão**: CRUD completo
- ✅ **Modal de Detalhes**: Clique no nome para visualizar
- ✅ **Navegação**: Sidebar com todos os módulos
- ✅ **Responsivo**: Funciona em celulares, tablets e desktop
- ✅ **Acesso Global**: Via NGROK de qualquer lugar

---

**Versão**: 1.0.0-LTS (Final Production)  
**Última atualização**: Março 2026  
**Status**: Fase 5 (Módulo de Auditoria, Parameters e Dashboard Configs) 100% Concluída ✅ | Homologado.  
**Acesso**: Local, remoto e externo (Via NGROK) configurados e Estáveis 🌐
