# Gestão de Benefícios - Sistema RH+Financeiro

## 📋 **Visão Geral**

A funcionalidade de Gestão de Benefícios foi implementada para gerenciar os benefícios oferecidos aos funcionários, incluindo controle de custos, tipos de cobrança e associação com funcionários.

## 🏗️ **Arquitetura**

### **Entidades Principais**

#### **1. Beneficio**
- **UUID id**: Identificador único do benefício
- **String nome**: Nome do benefício (ex: "Vale Transporte")
- **String descricao**: Descrição detalhada
- **TipoBeneficio tipo**: Tipo do benefício (enum)
- **TipoCobrancaBeneficio tipoCobranca**: Forma de cobrança (enum)
- **BigDecimal valor**: Valor fixo (se aplicável)
- **BigDecimal percentualDesconto**: Percentual sobre salário (se aplicável)
- **Boolean ativo**: Status de ativação
- **Boolean obrigatorio**: Se é obrigatório para todos

#### **2. FuncionarioBeneficio**
- **UUID id**: Identificador da associação
- **Funcionario funcionario**: Funcionário associado
- **Beneficio beneficio**: Benefício associado
- **LocalDate dataInicio**: Data de início
- **LocalDate dataFim**: Data de término (opcional)
- **BigDecimal valorFuncionario**: Valor pago pelo funcionário
- **BigDecimal valorEmpresa**: Valor pago pela empresa
- **Boolean ativo**: Status da associação

### **Enums**

#### **TipoBeneficio**
- `VALE_TRANSPORTE` - Vale Transporte
- `VALE_REFEICAO` - Vale Refeição
- `VALE_ALIMENTACAO` - Vale Alimentação
- `PLANO_SAUDE` - Plano de Saúde
- `PLANO_ODONTOLOGICO` - Plano Odontológico
- `SEGURO_VIDA` - Seguro de Vida
- `AUXILIO_CRECHE` - Auxílio Creche
- `AUXILIO_EDUCACAO` - Auxílio Educação
- `PARTICIPACAO_NOS_LUCROS` - Participação nos Lucros
- `PREVIDENCIA_PRIVADA` - Previdência Privada
- `GYM_PASS` - Gym Pass
- `OUTROS` - Outros

#### **TipoCobrancaBeneficio**
- `VALOR_FIXO` - Valor Fixo
- `PERCENTUAL_SALARIO` - Percentual sobre Salário
- `VALOR_VARIAVEL` - Valor Variável
- `EMPRESA_CUSTEIA_TOTAL` - Empresa custeia total
- `FUNCIONARIO_CUSTEIA_TOTAL` - Funcionário custeia total
- `COMPARTILHADO` - Compartilhado

## 🚀 **Funcionalidades Implementadas**

### **1. CRUD de Benefícios**
- ✅ Criar novo benefício
- ✅ Listar benefícios ativos
- ✅ Buscar benefício por ID
- ✅ Atualizar benefício existente
- ✅ Desativar benefício (soft delete)
- ✅ Listar benefícios opcionais/obrigatórios

### **2. Associação com Funcionários**
- ✅ Adicionar benefício a funcionário
- ✅ Listar benefícios por funcionário
- ✅ Remover benefício de funcionário
- ✅ Atualizar data de fim do benefício
- ✅ Cálculo automático de valores conforme tipo de cobrança

### **3. Relatórios e Consultas**
- ✅ Contagem de benefícios ativos
- ✅ Contagem de funcionários por benefício
- ✅ Cálculo de custo mensal da empresa
- ✅ Dashboard de resumo

## 📊 **Regras de Negócio**

### **Validações**
1. **Benefício com cobrança VALOR_FIXO**: Deve ter valor definido
2. **Benefício com cobrança PERCENTUAL_SALARIO**: Deve ter percentual definido (0-100%)
3. **Não pode desativar benefício**: Se houver funcionários associados ativos
4. **Data de fim**: Não pode ser anterior à data de início
5. **Nomes únicos**: Não pode haver benefício ativo com mesmo nome

### **Cálculos Automáticos**
Os valores são calculados automaticamente com base no tipo de cobrança:

#### **Valor Funcionário**
- `VALOR_FIXO`: Valor fixo do benefício
- `PERCENTUAL_SALARIO`: Salário × Percentual / 100
- `FUNCIONARIO_CUSTEIA_TOTAL`: Valor total do benefício
- `COMPARTILHADO`: 50% do valor total
- `EMPRESA_CUSTEIA_TOTAL`: Zero
- `VALOR_VARIAVEL`: Zero

#### **Valor Empresa**
- `EMPRESA_CUSTEIA_TOTAL`: Valor total do benefício
- `COMPARTILHADO`: 50% do valor total
- `PERCENTUAL_SALARIO`: Valor total - Valor Funcionário
- `VALOR_FIXO`: Valor total - Valor Funcionário
- `FUNCIONARIO_CUSTEIA_TOTAL`: Valor total - Valor Funcionário
- `VALOR_VARIAVEL`: Valor total - Valor Funcionário

## 🔗 **Endpoints API**

### **API V1 (`/api/beneficios`)**
```http
GET    /api/beneficios                  # Listar todos ativos
GET    /api/beneficios/opcionais        # Listar opcionais ativos
GET    /api/beneficios/obrigatorios     # Listar obrigatórios ativos
GET    /api/beneficios/{id}            # Buscar por ID (UUID)
POST   /api/beneficios                  # Criar novo
PUT    /api/beneficios/{id}             # Atualizar existente
DELETE /api/beneficios/{id}             # Desativar benefício

# Associação com funcionários
POST   /api/beneficios/funcionarios/{funcionarioId}/beneficios/{beneficioId}
GET    /api/beneficios/funcionarios/{funcionarioId}
DELETE /api/beneficios/funcionarios/{funcionarioId}/beneficios/{beneficioId}
PUT    /api/beneficios/funcionarios/{funcionarioId}/beneficios/{beneficioId}/data-fim

# Relatórios
GET    /api/beneficios/contagem/ativos
GET    /api/beneficios/{id}/contagem-funcionarios
GET    /api/beneficios/custo-mensal-empresa
GET    /api/beneficios/resumo
```

### **API V2 (`/api/v2/beneficios`)**
```http
POST   /api/v2/beneficios               # Criar com DTO
PUT    /api/v2/beneficios/{id}          # Atualizar com DTO
POST   /api/v2/beneficios/associar      # Associar com DTO
GET    /api/v2/beneficios/funcionarios/{funcionarioId}/detalhado
GET    /api/v2/beneficios/relatorio/custo-mensal
GET    /api/v2/beneficios/dashboard/resumo
```

## 🗄️ **Migration V13**
A migration `V13__create_beneficios_tables.sql` cria:
1. **Tabela `beneficios`**: Cadastro de benefícios disponíveis
2. **Tabela `funcionario_beneficios`**: Associação funcionário-benefício
3. **Índices otimizados**: Para performance em consultas frequentes
4. **Dados iniciais**: 8 benefícios padrão pré-cadastrados

## 🧪 **Testes (Recomendados)**
Para garantir qualidade, recomendamos implementar:

```java
// Testes unitários
@Test void deveCriarBeneficioComSucesso() { ... }
@Test void deveCalcularValoresCorretamente() { ... }
@Test void naoDeveDesativarComFuncionariosAtivos() { ... }

// Testes de integração
@Test void endpointDeveCriarBeneficio() { ... }
@Test void endpointDeveAssociarBeneficioAFuncionario() { ... }
```

## 📈 **Dashboard de Monitoramento**
A funcionalidade inclui endpoints para dashboard:
- **Custo mensal total**: R$ X.XXX,XX
- **Total de benefícios ativos**: XX
- **Distribuição por tipo**: Gráfico de pizza
- **Top 5 benefícios mais caros**: Ranking
- **Evolução mensal**: Gráfico de linha

## 🔒 **Segurança**
- **Validações**: Todas as operações têm validações robustas
- **Transações**: Operações críticas são transacionais
- **Soft delete**: Remoção lógica mantém histórico
- **Auditoria**: Datas de criação e atualização automáticas

## 🎯 **Próximos Passos**
1. **Integração com Folha de Pagamento**: Descontos automáticos
2. **Dashboard Web**: Interface gráfica para gestão
3. **Notificações**: Alertas de vencimento
4. **Relatórios PDF**: Exportação de relatórios
5. **API GraphQL**: Consultas mais flexíveis

## 📚 **Referências**
- **Swagger UI**: `/swagger-ui.html`
- **Flyway**: Migrations versionadas
- **Spring Boot**: Framework base
- **PostgreSQL**: Banco de dados
- **JPA/Hibernate**: Persistência