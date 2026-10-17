# Documento de Arquitetura

## Visão Geral

Este documento descreve a arquitetura do Sistema de Gestão de RH e Financeiro para Call Center.

## Decisões Arquiteturais

### ADR-001: Escolha da Arquitetura Monolítica Modular

**Status**: Aceito  
**Data**: Fevereiro 2026

**Contexto**:  
O sistema precisa operar em ambiente on-premise com infraestrutura simples, suportando até 500 usuários simultâneos.

**Decisão**:  
Adotar arquitetura monolítica modular com comunicação assíncrona entre módulos.

**Consequências**:
- ✅ Facilidade de deploy em ambiente on-premise
- ✅ Menor complexidade de infraestrutura
- ✅ Performance adequada para o escopo
- ✅ Separação clara de domínios sem complexidade de microsserviços
- ⚠️ Escalabilidade limitada (requer escalonamento vertical)

### ADR-002: Stack Tecnológico

**Status**: Aceito  
**Data**: Fevereiro 2026

**Contexto**:  
Necessidade de uma stack robusta, madura e com boa documentação para desenvolvimento ágil.

**Decisão**:  
Java 17 + Spring Boot 3.1.x + PostgreSQL 15

**Consequências**:
- ✅ Ecossistema maduro e estável
- ✅ Grande comunidade e suporte
- ✅ Facilidade de contratação de desenvolvedores
- ✅ Suporte de longo prazo (LTS)
- ⚠️ Curva de aprendizado para desenvolvedores iniciantes

### ADR-003: Comunicação entre Módulos

**Status**: Aceito  
**Data**: Fevereiro 2026

**Contexto**:  
Módulos precisam se comunicar de forma eficiente sem criar acoplamento forte.

**Decisão**:  
- Comunicação síncrona: REST para consultas
- Comunicação assíncrona: Eventos (ApplicationEventPublisher/RabbitMQ) para ações que não precisam de resposta imediata

**Consequências**:
- ✅ Desacoplamento entre módulos
- ✅ Melhor performance para operações assíncronas
- ✅ Resiliência (eventos podem ser reprocessados)
- ⚠️ Complexidade adicional no tratamento de erros assíncronos

## Estrutura de Módulos

### Módulo Core
Responsável por:
- Configurações comuns (Security, Web, Exception Handling)
- Classes utilitárias
- Anotações customizadas

### Módulo RH
Responsável por:
- Gestão de funcionários
- Admissões e demissões
- Controle de ponto
- Escalas de trabalho
- Férias e afastamentos

### Módulo Financeiro
Responsável por:
- Contas a pagar/receber
- Folha de pagamento
- Cálculo de rescisões
- Fluxo de caixa

### Módulo Comissões
Responsável por:
- Regras de comissão
- Cálculo de comissões
- Metas de vendas
- Integração com folha

### Módulo Documentos
Responsável por:
- Upload e armazenamento de documentos
- Controle de validade
- Geração de contratos
- Templates de documentos

### Módulo Dashboard
Responsável por:
- Relatórios gerenciais
- Dashboards interativos
- Exportação de dados (Excel, PDF)

### Módulo Notificações
Responsável por:
- Notificações internas
- Alertas automáticos
- Lembretes de documentos

## Padrões de Design

### Clean Architecture
Cada módulo segue os princípios da Clean Architecture:
- **Domain**: Entidades e regras de negócio puras
- **Application**: Casos de uso e orquestração
- **Infrastructure**: Implementações técnicas (repositórios, integrações)
- **API**: Interfaces REST

### Repository Pattern
Uso do padrão Repository para abstração do acesso a dados, facilitando testes e manutenção.

### Service Layer
Camada de serviços para orquestração de casos de uso e regras de negócio complexas.

### Event-Driven
Comunicação assíncrona via eventos para operações que não requerem resposta imediata.

## Segurança

### Autenticação
- JWT (JSON Web Tokens) com refresh token
- Stateless authentication

### Autorização
- RBAC (Role-Based Access Control)
- Roles: ADMIN, RH, FINANCEIRO, SUPERVISOR, VISUALIZADOR

### Criptografia
- Dados sensíveis criptografados com Jasypt
- Senhas com BCrypt

### Auditoria
- Log de todas as operações críticas
- Rastreabilidade de alterações

## Performance

### Cache
- Redis para cache de consultas frequentes
- Cache de configurações e dados de referência

### Otimizações
- Índices no banco de dados
- Paginação em todas as listagens
- Lazy loading onde apropriado
- Batch processing para operações em massa

## Monitoramento

### Métricas
- Prometheus para coleta de métricas
- Grafana para visualização (opcional)

### Logs
- Logback para logging estruturado
- Níveis de log configuráveis por ambiente

### Health Checks
- Spring Boot Actuator para health checks
- Endpoints: /actuator/health, /actuator/metrics

## Backup e Recuperação

### Estratégia de Backup
- Backup completo diário (02:00)
- Backup incremental a cada 6 horas
- Retenção de 30 dias

### Recuperação
- Testes mensais de restore
- Documentação de procedimentos de recuperação
