# Roadmap de Desenvolvimento — Status Real (outubro/2026)

> Este documento foi reconciliado com o estado real do codigo em 18/10/2026.
> Versao: 1.0.0-SNAPSHOT | Fases 1-4 concluidas | Fase 5 em andamento.
> Legenda: [x] implementado e verificado | [~] parcial | [ ] pendente

## Fase 1: Fundacao e Modulo RH Basico ✅ CONCLUIDA
- [x] Spring Boot 3.1.5 / Java 17, estrutura modular por packages (core, modulorh, modulofinanceiro, modulo_comissoes, moduloanalytics, modulorelatorios)
- [x] PostgreSQL + Flyway (V1-V17) alinhado ao docker-compose (5432/gestao_rh_financeiro)
- [x] Spring Security + JWT (access/refresh), RBAC com @EnableMethodSecurity ativo
- [x] CRUD funcionarios/departamentos/cargos/equipes/documentos
- [x] Redis no compose (uso atual: cache de parametros de sistema)
- [~] RabbitMQ provisionado porem sem producer/consumer — decisao pendente (integrar notificacoes ou remover)

## Fase 2: Modulo Financeiro ✅ CONCLUIDA
- [x] Contas pagar/receber, fluxo de caixa, agendamentos
- [x] Folha de pagamento com proventos/descontos e aplicacao automatica de comissoes
- [x] Contracheque em PDF via JasperReports (fallback dinamico em memoria)
- [~] DRE/impostos: valores reais para folha/saldo; alguns itens permanecem estimados

## Fase 3: Ponto, Escalas, Admissao/Demissao ✅ CONCLUIDA
- [x] Registro de ponto (batidas, espelho mensal, HE 50%/100%, ajuste supervisor)
- [x] Escalas CRUD (6x1, 5x2, 12x36, comercial, plantao) + grade semanal
- [~] Vinculacao em lote de funcionarios a escala (backlog)
- [x] Pipeline de admissao (checklist com progresso) e demissao (rescisao com aviso, ferias, 13o, multa FGTS)
- [x] Ferias e afastamentos (FE + BE)

## Fase 4: Comissoes e Relatorios Gerenciais ✅ CONCLUIDA
- [x] Regras de comissao com faixas, meta minima e acelerador (migracao V17)
- [x] Simulacao de comissao e apuracao mensal por operador
- [x] Aplicacao automatica da apuracao na folha (POST .../aplicar-comissoes/{mes} + botao na UI)
- [x] Central de relatorios: comissoes, provisoes de folha, turnover/absenteísmo, DRE
- [x] Exportacao CSV + PDF (JasperReports dinamico) de todos os relatorios gerenciais

## Fase 5: Seguranca, Testes e Infra 🚧 EM ANDAMENTO
### Seguranca ✅
- [x] CORS restritivo via CORS_ALLOWED_ORIGINS
- [x] Actuator/Swagger protegidos por role em prod
- [x] JWT secret e Jasypt obrigatorios por variavel de ambiente (sem defaults fracos)
- [x] Validacao de tipo de token (refresh != access); seed com senhas apenas em dev (ADMIN_SEED_PASSWORD)
- [x] Roles corrigidas (USER invalido removido; seed RH/FINANCEIRO)

### Testes ✅ (parcial)
- [x] Backend unitarios: CalculoRescisaoServiceTest, RegraComissaoTest, JwtTokenProviderTest (22 verdes)
- [x] Integracao Testcontainers: FlywaySchemaIntegrationTest (Postgres 15 + ddl-auto=validate) — roda onde ha Docker; pulado automaticamente sem daemon
- [x] Frontend: Vitest + React Testing Library configurados; format.test.ts + StatCard.test.tsx (9 verdes)
- [ ] Ampliar cobertura FE (holerite, formularios de admissao/demissao) e meta JaCoCo > 70%

### Infra/Deploy ✅
- [x] Dockerfile backend multi-stage
- [x] Dockerfile frontend (build + Nginx com proxy /api e SPA fallback)
- [x] docker-compose completo (postgres, redis, rabbitmq, backend, frontend)
- [x] CI GitHub Actions (.github/workflows/ci.yml): mvn verify + artifact JaCoCo, npm ci/lint/tsc/vitest/build, docker build
- [x] PMD + SpotBugs reativados no verify em modo gradual (relatorio sem quebrar build)
- [x] Dead code removido (test.py, auth-design-placeholder.ipynb)
- [ ] Checkstyle integrado ao ciclo Maven (config existe, plugin ainda nao ligado)

### Melhorias Call Center (backlog)
- [ ] WebSocket: alerta de meta batida e documentos a vencer (<5 dias)
- [ ] Pausas NR-17 (descanso 10 min / lanche 20 min) no modulo de ponto
- [ ] Exportacao AFD/AFDT (Portaria 671 MTE) para homologacao de ponto
- [ ] Gamificacao no mural/dashboard
- [ ] Gantt drag-and-drop para escalas
- [ ] Avaliacao de desempenho: formulario por tipo e feedback automatico (TODOs restantes)

## Proximos passos imediatos
1. Ligar fail-on-violation do PMD/SpotBugs apos zerar violacoes criticas
2. Expandir testes FE para componentes de calculo (holerite/rescisao)
3. Decidir destino do RabbitMQ (notificacoes assincronas ou remover do compose)
4. Implementar WebSocket + NR-17 + AFD como proxima release funcional
