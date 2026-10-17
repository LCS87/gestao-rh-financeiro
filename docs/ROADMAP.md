# Roadmap de Desenvolvimento

Este documento detalha o roadmap completo de desenvolvimento do sistema, dividido em 5 fases ao longo de 36 semanas.

## Fase 1: Fundação e Módulo RH Básico (Semanas 1-8)

### Semana 1: Setup do Projeto
- [x] Configuração do projeto Spring Boot
- [x] Estrutura modular inicial
- [x] Docker Compose para desenvolvimento
- [x] Configuração do banco de dados (PostgreSQL)
- [x] Configuração do Redis e RabbitMQ
- [x] Documentação inicial

### Semana 2: Autenticação e Autorização
- [ ] Implementação do Spring Security
- [ ] Sistema de autenticação JWT
- [ ] Refresh token
- [ ] Perfis de acesso (ADMIN, RH, FINANCEIRO, SUPERVISOR, VISUALIZADOR)
- [ ] Endpoints de login e logout
- [ ] Testes de autenticação

### Semana 3: CRUD de Funcionários
- [ ] CRUD completo de funcionários
- [ ] CRUD de departamentos
- [ ] CRUD de cargos
- [ ] CRUD de equipes
- [ ] Validações de negócio
- [ ] Testes unitários e de integração

### Semana 4: Dados Complementares
- [ ] CRUD de dependentes
- [ ] Histórico salarial
- [ ] Relacionamentos entre entidades
- [ ] Validações e regras de negócio
- [ ] Testes

### Semana 5: Módulo de Admissão
- [ ] Fluxo completo de admissão
- [ ] Checklist de documentos
- [ ] Controle de status da admissão
- [ ] Notificações automáticas
- [ ] Geração de contrato (template básico)
- [ ] Testes

### Semana 6: Módulo de Demissão
- [ ] Fluxo completo de demissão
- [ ] Cálculo básico de rescisão
- [ ] Tipos de demissão (sem justa causa, com justa causa, pedido)
- [ ] Aviso prévio (trabalhado/indenizado)
- [ ] Exame demissional
- [ ] Testes

### Semana 7: Gestão de Documentos
- [ ] Upload de documentos
- [ ] Armazenamento de arquivos
- [ ] Visualização de documentos
- [ ] Controle de validade
- [ ] Alertas de documentos vencendo
- [ ] Testes

### Semana 8: Testes e Documentação Fase 1
- [ ] Testes de integração completos
- [ ] Correções de bugs
- [ ] Documentação Swagger completa
- [ ] Manual básico do usuário
- [ ] Code review

## Fase 2: Controle de Ponto, Escala e Férias (Semanas 9-16)

### Semana 9: Modelagem de Escalas
- [ ] Entidade de escala de trabalho
- [ ] Tipos de escala (6x1, 5x2, 12x36, plantão)
- [ ] Configuração de horários
- [ ] Dias da semana de trabalho
- [ ] Intervalos de descanso
- [ ] Testes

### Semana 10: Registro de Ponto
- [ ] Registro manual de ponto
- [ ] Importação de ponto (planilha)
- [ ] Validações de ponto
- [ ] Histórico de registros
- [ ] Testes

### Semana 11: Cálculo de Horas
- [ ] Apuração de horas trabalhadas
- [ ] Cálculo de horas extras (50%, 100%)
- [ ] Cálculo de faltas
- [ ] Cálculo de atrasos
- [ ] Integração com escala
- [ ] Testes

### Semana 12: Módulo de Férias
- [ ] Solicitação de férias
- [ ] Aprovação de férias
- [ ] Cálculo de férias proporcionais
- [ ] Controle de período aquisitivo
- [ ] Alertas de férias a vencer
- [ ] Testes

### Semana 13: Controle de Afastamentos
- [ ] Registro de atestados
- [ ] Licenças médicas
- [ ] Licenças maternidade/paternidade
- [ ] Outros tipos de afastamento
- [ ] Integração com folha de ponto
- [ ] Testes

### Semana 14: Dashboard de RH
- [ ] Indicadores de turnover
- [ ] Taxa de absenteísmo
- [ ] Gráficos de admissões/demissões
- [ ] Relatórios de ponto
- [ ] Visualização em tempo real
- [ ] Testes

### Semana 15: Sistema de Alertas
- [ ] Alertas de documentos vencendo
- [ ] Alertas de férias a vencer
- [ ] Alertas de exames periódicos
- [ ] Notificações internas
- [ ] Configuração de alertas
- [ ] Testes

### Semana 16: Testes e Documentação Fase 2
- [ ] Testes de integração completos
- [ ] Correções de bugs
- [ ] Atualização da documentação
- [ ] Code review

## Fase 3: Módulo Financeiro e Folha de Pagamento (Semanas 17-24)

### Semana 17: Modelagem Financeira
- [ ] Entidade de contas a pagar
- [ ] Entidade de contas a receber
- [ ] Categorias de contas
- [ ] Fornecedores e clientes
- [ ] Testes

### Semana 18: Fluxo de Contas
- [ ] CRUD de contas a pagar
- [ ] CRUD de contas a receber
- [ ] Controle de status (pendente, pago, vencido)
- [ ] Contas recorrentes
- [ ] Testes

### Semana 19: Cálculo da Folha
- [ ] Algoritmo de cálculo da folha
- [ ] Cálculo de salário base
- [ ] Cálculo de INSS
- [ ] Cálculo de IRRF
- [ ] Descontos (VT, VR, plano saúde)
- [ ] Testes

### Semana 20: Integração Ponto → Folha
- [ ] Integração com horas trabalhadas
- [ ] Desconto de faltas
- [ ] Cálculo de horas extras na folha
- [ ] Atrasos e descontos
- [ ] Testes

### Semana 21: Geração de Contracheques
- [ ] Template de contracheque (JasperReports)
- [ ] Geração de PDF
- [ ] Envio automático (email)
- [ ] Histórico de contracheques
- [ ] Testes

### Semana 22: Agendamento Automático
- [ ] Agendamento da folha (todo dia 5)
- [ ] Processamento automático
- [ ] Notificações de processamento
- [ ] Tratamento de erros
- [ ] Testes

### Semana 23: Integração RH → Financeiro
- [ ] Eventos de admissão (criação de contas)
- [ ] Eventos de demissão (encerramento de contas)
- [ ] Sincronização de dados
- [ ] Testes

### Semana 24: Testes e Documentação Fase 3
- [ ] Testes de integração completos
- [ ] Correções de bugs
- [ ] Atualização da documentação
- [ ] Code review

## Fase 4: Comissões, Relatórios e Maturidade (Semanas 25-32)

### Semana 25: Modelagem de Comissões
- [ ] Entidade de regras de comissão
- [ ] Metas de vendas
- [ ] Configuração de percentuais
- [ ] Regras progressivas
- [ ] Testes

### Semana 26: Integração com Vendas
- [ ] Importação de vendas (planilha)
- [ ] API de vendas (futuro)
- [ ] Validação de vendas
- [ ] Histórico de vendas
- [ ] Testes

### Semana 27: Calculadora de Comissões
- [ ] Algoritmo de cálculo de comissões
- [ ] Aplicação de regras
- [ ] Cálculo de bônus por meta
- [ ] Validações
- [ ] Testes

### Semana 28: Integração Comissões → Folha
- [ ] Inclusão de comissões na folha
- [ ] Cálculo do valor líquido final
- [ ] Histórico de comissões pagas
- [ ] Testes

### Semana 29: Relatórios Gerenciais
- [ ] Relatórios de RH (Excel, PDF)
- [ ] Relatórios financeiros
- [ ] Relatórios de comissões
- [ ] Exportação de dados
- [ ] Testes

### Semana 30: Dashboard Financeiro
- [ ] Fluxo de caixa
- [ ] Projeções financeiras
- [ ] Indicadores financeiros
- [ ] Gráficos e visualizações
- [ ] Testes

### Semana 31: Sistema de Backup
- [ ] Scripts de backup automático
- [ ] Backup do banco de dados
- [ ] Backup de arquivos
- [ ] Retenção de backups
- [ ] Testes de restore

### Semana 32: Testes Completos
- [ ] Testes unitários (cobertura > 70%)
- [ ] Testes de integração
- [ ] Testes de sistema
- [ ] Testes de performance
- [ ] Correções finais
- [ ] Code review completo

## Fase 5: Homologação e Implantação (Semanas 33-36)

### Semana 33: Ambiente de Homologação
- [ ] Configuração do servidor on-premise
- [ ] Instalação de dependências
- [ ] Deploy da aplicação
- [ ] Configuração de backup
- [ ] Monitoramento

### Semana 34: Testes com Usuários
- [ ] Testes com equipe de RH
- [ ] Testes com equipe financeira
- [ ] Coleta de feedback
- [ ] Identificação de ajustes necessários

### Semana 35: Ajustes e Documentação
- [ ] Correções baseadas no feedback
- [ ] Ajustes de usabilidade
- [ ] Manual completo do usuário
- [ ] Vídeos tutoriais (opcional)
- [ ] Documentação técnica final

### Semana 36: Deploy em Produção
- [ ] Deploy em produção
- [ ] Migração de dados (se houver)
- [ ] Treinamento de usuários
- [ ] Acompanhamento inicial
- [ ] Suporte pós-lançamento

## Métricas de Sucesso

### Técnicas
- [ ] 100% das funcionalidades planejadas implementadas
- [ ] Cobertura de testes > 70%
- [ ] API documentada com Swagger
- [ ] Código seguindo boas práticas (Clean Code, SOLID)
- [ ] Deploy em produção realizado com sucesso

### Funcionais
- [ ] Redução do tempo de admissão de 3 dias para 2 horas
- [ ] Eliminação de planilhas manuais (100% digital)
- [ ] Folha de pagamento calculada em minutos
- [ ] Relatórios disponíveis em tempo real
- [ ] Usuários capacitados e utilizando o sistema diariamente

## Próximos Passos

1. Iniciar desenvolvimento da Semana 2 (Autenticação)
2. Configurar CI/CD (opcional)
3. Definir padrões de código e revisão
4. Estabelecer processo de deploy
