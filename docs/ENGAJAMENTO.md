# Engajamento (Mural Digital) - Sistema RH+Financeiro

## 📋 **Visão Geral**

A funcionalidade de Engajamento implementa um **mural digital completo** para comunicação interna da empresa. Inclui postagens, comentários, reações, enquetes e sistema de reconhecimento entre colegas.

## 🏗️ **Arquitetura**

### **Entidades Principais**

#### **1. PostagemMural** (Postagens no mural)
- **UUID id**: Identificador único
- **Funcionario autor**: Autor da postagem
- **String conteudo**: Conteúdo da postagem (500 caracteres)
- **String titulo**: Título opcional
- **TipoPostagem tipo**: Tipo da publicação (enum)
- **VisibilidadePostagem visibilidade**: Quem pode ver (enum)
- **Boolean fixada**: Se está fixada no topo
- **Boolean ativa**: Status de ativação
- **List<ComentarioPostagem> comentarios**: Comentários na postagem
- **List<CurtidaPostagem> curtidas**: Reações na postagem

#### **2. ComentarioPostagem** (Comentários)
- **UUID id**: Identificador único
- **PostagemMural postagem**: Postagem relacionada
- **Funcionario autor**: Autor do comentário
- **String conteudo**: Conteúdo do comentário (1000 caracteres)
- **ComentarioPostagem comentarioPai**: Para respostas a comentários

#### **3. CurtidaPostagem** (Reações/Likes)
- **UUID id**: Identificador único
- **PostagemMural postagem**: Postagem curtida
- **Funcionario funcionario**: Funcionário que reagiu
- **TipoReacao tipoReacao**: Tipo de reação (enum)
- **UniqueConstraint**: Um funcionário só pode ter uma reação por postagem

#### **4. EnqueteMural** (Enquetes)
- **UUID id**: Identificador único
- **Funcionario autor**: Autor da enquete
- **String pergunta**: Pergunta da enquete
- **Boolean anonima**: Se os votos são anônimos
- **Boolean permiteMultiplaEscolha**: Se permite múltiplas opções
- **LocalDateTime dataExpiracao**: Data de expiração
- **List<OpcaoEnquete> opcoes**: Opções de resposta
- **List<VotoEnquete> votos**: Votos registrados

#### **5. OpcaoEnquete** (Opções de enquete)
- **UUID id**: Identificador único
- **EnqueteMural enquete**: Enquete relacionada
- **String texto**: Texto da opção
- **Integer ordem**: Ordem de exibição

#### **6. VotoEnquete** (Votos)
- **UUID id**: Identificador único
- **EnqueteMural enquete**: Enquete votada
- **Funcionario funcionario**: Funcionário que votou
- **OpcaoEnquete opcao**: Opção escolhida
- **UniqueConstraint**: Um funcionário só pode votar uma vez por opção

#### **7. ReconhecimentoColaborador** (Sistema de "parabéns")
- **UUID id**: Identificador único
- **Funcionario remetente**: Quem reconhece
- **Funcionario destinatario**: Quem é reconhecido
- **TipoReconhecimento tipo**: Tipo de reconhecimento (enum)
- **String mensagem**: Mensagem personalizada
- **Boolean publico**: Se é visível para todos
- **CheckConstraint**: Remetente ≠ Destinatário

### **Enums**

#### **TipoPostagem**
- `PUBLICACAO` - Publicação normal
- `ANUNCIO` - Anúncio importante
- `NOTICIA` - Notícia da empresa
- `EVENTO` - Evento
- `DICA` - Dica ou sugestão
- `RECONHECIMENTO` - Reconhecimento público
- `RESULTADO` - Resultado ou conquista
- `OUTRO` - Outro tipo

#### **VisibilidadePostagem**
- `PUBLICO` - Todos os funcionários
- `DEPARTAMENTO` - Apenas departamento
- `EQUIPE` - Apenas equipe
- `PRIVADO` - Apenas selecionados

#### **TipoReacao** (Emojis)
- `CURTIR` - 👍 Curtir
- `AMAR` - ❤️ Amar
- `RIR` - 😂 Rir
- `SURPREENDER` - 😮 Surpreender
- `TRISTE` - 😢 Triste
- `RAIVA` - 😠 Raiva

#### **TipoReconhecimento**
- `PARABENS` - 🎉 Parabéns
- `OBRIGADO` - 🙏 Obrigado
- `EXCELENTE_TRABALHO` - 🌟 Excelente Trabalho
- `COLABORACAO` - 🤝 Colaboração
- `INOVACAO` - 💡 Inovação
- `LIDERANCA` - 👑 Liderança
- `MELHORIA_CONTINUA` - 📈 Melhoria Contínua
- `ESPIRITO_EQUIPE` - 🏆 Espírito de Equipe

## 🚀 **Funcionalidades Implementadas**

### **1. Mural Digital (Postagens)**
- ✅ Criar postagens com diferentes tipos e visibilidade
- ✅ Listar postagens ativas (paginação)
- ✅ Postagens fixadas no topo
- ✅ Comentários hierárquicos (respostas a comentários)
- ✅ Sistema de reações com emojis (toggle)
- ✅ Edição e desativação de postagens
- ✅ Busca por termo

### **2. Enquetes e Pesquisas de Clima**
- ✅ Criar enquetes com múltiplas opções
- ✅ Votação anônima ou identificada
- ✅ Enquetes com data de expiração
- ✅ Permitir múltipla escolha ou única
- ✅ Resultados em tempo real com percentuais
- ✅ Validação de votos duplicados

### **3. Sistema de Reconhecimento**
- ✅ Reconhecimento entre colegas com tipos específicos
- ✅ Mensagens personalizadas
- ✅ Reconhecimentos públicos ou privados
- ✅ Listagem de reconhecimentos recebidos/enviados
- ✅ Dashboard de funcionários mais reconhecidos

### **4. Dashboard e Métricas**
- ✅ Dashboard completo de engajamento
- ✅ Postagens mais comentadas/curtidas
- ✅ Enquetes mais votadas
- ✅ Top funcionários reconhecidos
- ✅ Distribuição por tipo de conteúdo
- ✅ Feed personalizado por departamento

## 📊 **Regras de Negócio**

### **Postagens**
1. **Conteúdo obrigatório**: Postagens devem ter conteúdo
2. **Fixação limitada**: Apenas administradores podem fixar (implementar regra)
3. **Visibilidade**: Controla quem pode ver cada postagem
4. **Edição**: Apenas autor pode editar
5. **Desativação**: Remove sem deletar (soft delete)

### **Enquetes**
1. **Opções obrigatórias**: Mínimo 2 opções
2. **Voto único por opção**: Usuário não pode votar duas vezes na mesma opção
3. **Múltipla escolha**: Configurável na criação
4. **Data de expiração**: Opcional, encerra votação automaticamente
5. **Resultados anônimos**: Se configurado, não mostra quem votou

### **Reconhecimentos**
1. **Auto-reconhecimento proibido**: Não pode se reconhecer
2. **Mensagem obrigatória**: Deve ter conteúdo
3. **Visibilidade**: Configurável pelo remetente
4. **Desativação**: Apenas autor pode desativar

### **Reações**
1. **Toggle system**: Clicar novamente remove a reação
2. **Troca de reação**: Manter clique troca tipo de reação
3. **Uma por usuário**: Apenas uma reação por funcionário por postagem

## 🔗 **Endpoints API**

### **Base URL**: `/api/engajamento`

#### **Postagens**
```http
POST   /postagens                     # Criar nova postagem
GET    /postagens                    # Listar postagens (paginação)
GET    /postagens/fixadas            # Listar postagens fixadas
GET    /postagens/autor/{autorId}    # Postagens por autor
GET    /postagens/{id}               # Buscar por ID
PUT    /postagens/{id}               # Atualizar postagem
PATCH  /postagens/{id}/fixar         # Alternar fixação
DELETE /postagens/{id}               # Desativar postagem
```

#### **Comentários**
```http
POST   /postagens/{postagemId}/comentarios    # Adicionar comentário
GET    /postagens/{postagemId}/comentarios    # Listar comentários
PUT    /comentarios/{id}                      # Atualizar comentário
DELETE /comentarios/{id}                      # Desativar comentário
```

#### **Reações**
```http
POST   /postagens/{postagemId}/reacoes        # Adicionar reação
GET    /postagens/{postagemId}/reacoes        # Listar reações
GET    /postagens/{postagemId}/reacoes/distribuicao  # Distribuição por tipo
```

#### **Enquetes**
```http
POST   /enquetes                             # Criar enquete
GET    /enquetes                            # Listar enquetes ativas
GET    /enquetes/{id}                       # Buscar por ID
POST   /enquetes/{enqueteId}/votar          # Votar em enquete
GET    /enquetes/{id}/resultados            # Obter resultados
```

#### **Reconhecimentos**
```http
POST   /reconhecimentos                     # Criar reconhecimento
GET    /reconhecimentos                    # Listar reconhecimentos públicos
GET    /reconhecimentos/recebidos/{destinatarioId}
GET    /reconhecimentos/enviados/{remetenteId}
DELETE /reconhecimentos/{id}               # Desativar reconhecimento
```

#### **Dashboard e Feed**
```http
GET    /dashboard                          # Dashboard completo
GET    /feed/{funcionarioId}               # Feed personalizado
```

## 🗄️ **Migration V14**
A migration `V14__create_engajamento_tables.sql` cria:
1. **7 tabelas principais**: Postagens, Comentários, Curtidas, Enquetes, Opções, Votos, Reconhecimentos
2. **Índices otimizados**: Para performance em consultas frequentes
3. **Constraints de validação**: Garantia de integridade dos dados
4. **View de dashboard**: `view_dashboard_engajamento`
5. **Dados iniciais**: Postagens de exemplo

## 🧪 **Características Técnicas**
- **UUID**: Todos IDs usam UUID
- **Paginação**: Todas as listagens suportam paginação
- **Soft Delete**: Remoção lógica mantém histórico
- **Transações**: Operações críticas são transacionais
- **Validações**: Bean Validation com mensagens customizadas
- **Performance**: Índices otimizados para consultas frequentes
- **Swagger**: Documentação API completa

## 📈 **Dashboard de Métricas**
O sistema fornece dashboard com:
- **Totais**: Postagens, comentários, curtidas, enquetes, reconhecimentos
- **Rankings**: Postagens mais comentadas/curtidas
- **Top funcionários**: Mais reconhecidos e que mais reconhecem
- **Distribuição**: Por tipo de conteúdo
- **Feed personalizado**: Baseado em departamento e interesses

## 🔒 **Segurança e Controle de Acesso**
- **Validações**: Todas as operações têm validações robustas
- **Controle de edição**: Apenas autor pode editar
- **Visibilidade**: Controle granular de quem vê o quê
- **Auditoria**: Datas de criação/edição automáticas
- **Anonimato**: Configurável para enquetes

## 🎯 **Próximas Melhorias**
1. **Notificações em tempo real**: WebSocket para atualizações
2. **Upload de arquivos**: Imagens e documentos
3. **Marcações (@mentions)**: Marcar colegas em postagens
4. **Hashtags**: Categorização de conteúdo
5. **Relatórios PDF/Excel**: Exportação de métricas
6. **API GraphQL**: Consultas mais flexíveis
7. **Frontend React/Angular**: Interface web moderna
8. **Mobile app**: Aplicativo para smartphones
9. **Integração com email**: Notificações por email
10. **Moderação**: Ferramentas para administradores

## 📚 **Referências**
- **Swagger UI**: `/swagger-ui.html`
- **Migration V14**: `src/main/resources/db/migration/V14__create_engajamento_tables.sql`
- **Entidades**: Pacote `br.com.callcenter.modulorh.domain.entity`
- **Enums**: Pacote `br.com.callcenter.modulorh.domain.enums`
- **Repositories**: Pacote `br.com.callcenter.modulorh.infrastructure.repository`
- **Service**: `EngajamentoService`
- **Controller**: `EngajamentoController`