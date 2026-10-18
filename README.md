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

O sistema é um **Monolito Modular Maven** (um único JAR) organizado por packages em `src/main/java/br/com/callcenter/`:

```
br.com.callcenter/
├── core/                  # Segurança (JWT), config, auditoria, usuários, parâmetros
├── modulorh/              # Funcionários, departamentos, ponto, escalas, férias,
│                          # admissão/demissão, documentos, vendas, benefícios, engajamento
├── modulofinanceiro/      # Contas, folha de pagamento, agendamentos, contracheques
├── modulo_comissoes/      # Regras de comissão, apuração e simulação
├── moduloanalytics/       # Indicadores e analytics (parcial — placeholders restantes)
└── modulorelatorios/      # Relatórios gerenciais (CSV + PDF via JasperReports)
```

Dentro de cada package, a organização segue camadas **API (Controller) → Application (Service) → Domain (Entidades/regras) → Infrastructure (Repositories)**, sem fronteiras físicas de módulo Maven.

> ⚠️ *Planejado vs real*: módulos separados (ex.: `modulo-notificacoes`, `modulo-dashboard`) e RabbitMQ como mensageria **não existem no código** — documentos ficam dentro de `modulorh` e o RabbitMQ roda apenas no docker-compose sem producer/consumer. Ver seção "Pendências".

## 🛠️ Tecnologias

### Backend
- **Java 17 LTS**
- **Spring Boot 3.1.x**
- **Spring Security + JWT** (com `@EnableMethodSecurity` / RBAC ativo)
- **Spring Data JPA + Hibernate**
- **PostgreSQL 15** (docker-compose: porta 5432, banco `gestao_rh_financeiro`)
- **Redis 7** (declarado; uso atual limitado a `@Cacheable` em parâmetros)
- **Flyway** (V1–V9, V12–V17; perfil base com Flyway ativo apontando para o compose)
- **JasperReports** (Relatórios PDF e contracheque)
- **SpringDoc OpenAPI** (Documentação API)

### Frontend
- **React 19** + **TypeScript**
- **Vite 7** (build; dev server na porta **3007**)
- **React Router** (navegação)
- **TanStack Query** (dados)
- **Tailwind CSS** (estilos)
- **Axios** (API)
- **Lucide React** (ícones)

### Infraestrutura
- **Docker & Docker Compose** (apenas infra: Postgres, Redis, RabbitMQ — Dockerfile da aplicação ainda pendente)
- ~~Nginx / Prometheus / Grafana~~ (planejados, **não implementados**)

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
- **Interface web**: http://localhost:3007 (porta configurada no `vite.config.ts`)
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html (autenticação exigida em produção)
- **API Docs**: http://localhost:8080/api-docs
- **Health**: http://localhost:8080/actuator/health (protegido — apenas ROLE_TI/ADMIN)
- **PostgreSQL**: localhost:5432 / banco `gestao_rh_financeiro` (via docker-compose)

#### 📱 Acesso Remoto (Mesma Rede Wi-Fi)
- Ajuste o IP da máquina local (`ipconfig`/`ifconfig`) e acesse via rede: `http://SEU_IP:3007` (Vite configurado com `host: 0.0.0.0`).

#### 🌐 Acesso Externo
> ⚠️ **Nota de segurança**: exposição via túnel (ngrok, port forwarding etc.) é prática de laboratório/desenvolvimento, **não recomendada para produção**. Para deploy real, use HTTPS com Nginx/reverse proxy autenticado e CORS restrito. O pipeline CI/CD e os Dockerfiles da aplicação ainda estão pendentes na Fase 5.

O frontend faz proxy de `/api` para o backend (porta configurada no `vite.config.ts`).

8. **Login padrão (exclusivo para desenvolvimento)**

- **Usuário**: `admin` / **Senha**: definida em `ADMIN_SEED_PASSWORD` (env) — o seed com senha padrão `admin123` só é ativado no profile `dev`.
- O `DataInitializer` cria usuários ADMIN, TI, RH, FINANCEIRO, SUPERVISOR e VISUALIZADOR na primeira execução (somente em dev; em produção use migração/IDP com senhas fortes).

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

### Fase 5: Módulo de Auditoria, Configurações e Implantação (Semanas 33-36) 🚧
- ✅ Entidade e Tabela `ParametroSistema` (Chaves Dinâmicas)
- ✅ Injeção Dinâmica da `META_VENDAS_DIARIA` em Tela de Vendas
- ✅ Injeção de Interceptador AOP (`@Audited`) nas Rotas de Delete e Criar
- ✅ Painel de **Settings.tsx** (Usuários, Auditoria de Logs Seguros, e Parâmetros Front-End)
- ✅ Segurança P0/P1: CORS restrito, Actuator/Swagger protegidos, secrets obrigatórios (JWT/Jasypt), refresh token validado, seed endurecido
- ✅ Testes unitários backend (JUnit 5 + Mockito): cálculo de rescisão, regras de comissão, JWT provider
- ⏳ Pendente: testes de integração com Testcontainers, Vitest + RTL no frontend, Dockerfile da app + CI/CD, reativação PMD/SpotBugs/Checkstyle
- ~~Testes com usuários reais finalizados~~ (não confirmado — cobertura ainda parcial)
- ~~Deploy finalizado~~ (não realizado — sem Dockerfile da aplicação nem pipeline CI)

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

### Módulo Testes 🚧 (em andamento)
- **Testes unitários backend**: JUnit 5 + Mockito — cálculo de rescisão, regras de comissão, JWT provider (`src/test/java`)
- ⏳ Pendente: testes de integração com Testcontainers, Vitest + React Testing Library no frontend, meta de cobertura > 70%
- ~~Deploy / sistema completo de implantação~~ (não realizado — Dockerfile da app e CI ausentes)

### Frontend Moderno ✅ (parcial)
- **React 19 + TypeScript**: Interface moderna e type-safe
- **Shadcn/UI + Tailwind**: Design system profissional
- **Navegação Completa**: Sidebar funcional com routing (inclui Documentos, Avaliações, Mural, Benefícios, Ponto, Escalas, Admissão/Demissão, Comissões e Relatórios)
- **Dashboards Inteligentes**: Indicadores reais via `/rh/dashboard/indicadores`
- **Simulador de Comissão e Apuração Mensal**: tela Comissoes.tsx integrada ao backend
- **Central de Relatórios**: exportação CSV e PDF (provisões, turnover/absenteísmo, DRE)
- **Modais Interativos**: Popup de detalhes dos funcionários
- **Formulários Avançados**: validação com Zod + React Hook Form
- ❌ Não implementados: Gamificação/Kanban de admissão completo, Gantt drag-and-drop de escalas (versão atual é grade semanal), acesso externo "multi-dispositivo" como produto

### Funcionalidades Técnicas ✅
- **Autenticação**: JWT com refresh token
- **Autorização**: RBAC com roles (ADMIN, RH, FINANCEIRO, etc.)
- **Segurança**: Jasypt para dados sensíveis
- **Cache**: Redis para performance (uso atual: `@Cacheable` em parâmetros de sistema)
- **Mensageria**: RabbitMQ disponível no docker-compose ⚠️ *sem producer/consumer implementados no código (decisão pendente: integrar ou remover)*
- **Agendamento**: Tarefas automáticas com @Scheduled
- **Relatórios**: JasperReports para PDFs
- **Documentação**: OpenAPI/Swagger integrado

---

## 🌐 Acesso ao Sistema

### 📋 Resumo de Acesso (desenvolvimento local)

| Tipo | URL | Requisitos |
|------|-----|------------|
| 🏠 **Interface web** | http://localhost:3007 | `npm run dev` no frontend |
| ⚙️ **API** | http://localhost:8080 | `mvn spring-boot:run` + Postgres via compose |

> Acesso externo (ngrok/VPN/port forwarding) é prática opcional de laboratório e não faz parte do produto. Ver a seção "Acesso Externo" acima para a nota de segurança.

### 📱 Funcionalidades Disponíveis

- ✅ **Cadastro Completo**: 45+ campos para funcionários
- ✅ **Edição e Exclusão**: CRUD completo
- ✅ **Modal de Detalhes**: Clique no nome para visualizar
- ✅ **Navegação**: Sidebar com todos os módulos (RH, Financeiro, Ponto/Escalas, Comissões, Relatórios)
- ✅ **Responsivo**: Funciona em celulares, tablets e desktop

---

## ⚠️ Pendências Conhecidas (Fase 5 e backlog)

1. **CI/CD e Docker**: sem Dockerfile da aplicação, sem serviços app/frontend no compose, sem pipeline (.github/workflows ausente).
2. **Testes**: Testcontainers (integração), Vitest + RTL (frontend); cobertura atual abaixo da meta de 70%.
3. **Qualidade estática**: PMD/SpotBugs comentados no pom.xml; Checkstyle configurado mas não integrado ao build.
4. **Dead code**: `test.py` e `auth-design-placeholder.ipynb` ainda presentes na raiz.
5. **RabbitMQ**: decidir integrar (notificações/metas) ou remover do compose.
6. **Placeholders restantes**: alguns indicadores de analytics ainda estimados.
7. **Backlog call center**: WebSocket (metas/documentos a vencer), pausas NR-17, exportação AFD/AFDT (Portaria 671), vinculação em lote de escalas, conclusão do módulo de avaliação de desempenho.
8. **docs/ROADMAP.md**: desatualizado — precisa ser reconciliado com o status real acima.

---

**Versão**: 1.0.0-SNAPSHOT  
**Última atualização**: Outubro 2026  
**Status**: Fases 1–4 concluídas ✅ | Fase 5 em andamento 🚧 (segurança P0/P1 e testes unitários backend entregues; pendentes: CI/CD, Dockerfile da app, testes frontend, Testcontainers).  
**Acesso**: Local (Vite na porta 3007) — deploy externo ainda não configurado.
