-- Migration V14: Criação das tabelas de Engajamento (Mural Digital)
-- Data: 03/09/2026
-- Descrição: Sistema completo de comunicação interna, enquetes e reconhecimento

-- Tabela de postagens no mural
CREATE TABLE postagens_mural (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    autor_id UUID NOT NULL,
    conteudo VARCHAR(500) NOT NULL,
    titulo VARCHAR(100),
    tipo VARCHAR(50) NOT NULL DEFAULT 'PUBLICACAO',
    visibilidade VARCHAR(50) NOT NULL DEFAULT 'PUBLICO',
    data_publicacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_edicao TIMESTAMP,
    fixada BOOLEAN NOT NULL DEFAULT false,
    ativa BOOLEAN NOT NULL DEFAULT true,
    url_imagem VARCHAR(500),
    url_anexo VARCHAR(500),
    
    CONSTRAINT fk_postagem_autor FOREIGN KEY (autor_id) REFERENCES funcionarios(id),
    CONSTRAINT chk_postagem_tipo_valido CHECK (tipo IN (
        'PUBLICACAO', 'ANUNCIO', 'NOTICIA', 'EVENTO', 'DICA', 
        'RECONHECIMENTO', 'RESULTADO', 'OUTRO'
    )),
    CONSTRAINT chk_postagem_visibilidade_valida CHECK (visibilidade IN (
        'PUBLICO', 'DEPARTAMENTO', 'EQUIPE', 'PRIVADO'
    ))
);

-- Índices para postagens
CREATE INDEX idx_postagens_autor ON postagens_mural(autor_id);
CREATE INDEX idx_postagens_data_publicacao ON postagens_mural(data_publicacao DESC);
CREATE INDEX idx_postagens_fixadas ON postagens_mural(fixada) WHERE fixada = true;
CREATE INDEX idx_postagens_ativas ON postagens_mural(ativa) WHERE ativa = true;

-- Tabela de comentários nas postagens
CREATE TABLE comentarios_postagem (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    postagem_id UUID NOT NULL,
    autor_id UUID NOT NULL,
    conteudo VARCHAR(1000) NOT NULL,
    data_comentario TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_edicao TIMESTAMP,
    ativo BOOLEAN NOT NULL DEFAULT true,
    comentario_pai_id UUID,
    
    CONSTRAINT fk_comentario_postagem FOREIGN KEY (postagem_id) REFERENCES postagens_mural(id),
    CONSTRAINT fk_comentario_autor FOREIGN KEY (autor_id) REFERENCES funcionarios(id),
    CONSTRAINT fk_comentario_pai FOREIGN KEY (comentario_pai_id) REFERENCES comentarios_postagem(id),
    CONSTRAINT chk_comentario_conteudo_nao_vazio CHECK (LENGTH(TRIM(conteudo)) > 0)
);

-- Índices para comentários
CREATE INDEX idx_comentarios_postagem ON comentarios_postagem(postagem_id);
CREATE INDEX idx_comentarios_autor ON comentarios_postagem(autor_id);
CREATE INDEX idx_comentarios_data ON comentarios_postagem(data_comentario DESC);
CREATE INDEX idx_comentarios_ativos ON comentarios_postagem(ativo) WHERE ativo = true;

-- Tabela de curtidas/reactions nas postagens
CREATE TABLE curtidas_postagem (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    postagem_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    tipo_reacao VARCHAR(50) NOT NULL DEFAULT 'CURTIR',
    data_curtida TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_curtida_postagem FOREIGN KEY (postagem_id) REFERENCES postagens_mural(id),
    CONSTRAINT fk_curtida_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id),
    CONSTRAINT chk_tipo_reacao_valido CHECK (tipo_reacao IN (
        'CURTIR', 'AMAR', 'RIR', 'SURPREENDER', 'TRISTE', 'RAIVA'
    )),
    CONSTRAINT uq_curtida_unica UNIQUE (postagem_id, funcionario_id)
);

-- Índices para curtidas
CREATE INDEX idx_curtidas_postagem ON curtidas_postagem(postagem_id);
CREATE INDEX idx_curtidas_funcionario ON curtidas_postagem(funcionario_id);

-- Tabela de enquetes
CREATE TABLE enquetes_mural (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    autor_id UUID NOT NULL,
    pergunta VARCHAR(500) NOT NULL,
    descricao VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_expiracao TIMESTAMP,
    anonima BOOLEAN NOT NULL DEFAULT false,
    ativa BOOLEAN NOT NULL DEFAULT true,
    permite_multipla_escolha BOOLEAN NOT NULL DEFAULT false,
    
    CONSTRAINT fk_enquete_autor FOREIGN KEY (autor_id) REFERENCES funcionarios(id),
    CONSTRAINT chk_enquete_pergunta_nao_vazia CHECK (LENGTH(TRIM(pergunta)) > 0)
);

-- Índices para enquetes
CREATE INDEX idx_enquetes_autor ON enquetes_mural(autor_id);
CREATE INDEX idx_enquetes_data_criacao ON enquetes_mural(data_criacao DESC);
CREATE INDEX idx_enquetes_ativas ON enquetes_mural(ativa) WHERE ativa = true;

-- Tabela de opções para enquetes
CREATE TABLE opcoes_enquete (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enquete_id UUID NOT NULL,
    texto VARCHAR(500) NOT NULL,
    ordem INTEGER NOT NULL,
    
    CONSTRAINT fk_opcao_enquete FOREIGN KEY (enquete_id) REFERENCES enquetes_mural(id),
    CONSTRAINT chk_opcao_texto_nao_vazio CHECK (LENGTH(TRIM(texto)) > 0),
    CONSTRAINT chk_opcao_ordem_positiva CHECK (ordem >= 0)
);

-- Índices para opções de enquete
CREATE INDEX idx_opcoes_enquete ON opcoes_enquete(enquete_id);
CREATE INDEX idx_opcoes_ordem ON opcoes_enquete(ordem);

-- Tabela de votos em enquetes
CREATE TABLE votos_enquete (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enquete_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    opcao_id UUID NOT NULL,
    data_voto TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_voto_enquete FOREIGN KEY (enquete_id) REFERENCES enquetes_mural(id),
    CONSTRAINT fk_voto_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id),
    CONSTRAINT fk_voto_opcao FOREIGN KEY (opcao_id) REFERENCES opcoes_enquete(id),
    CONSTRAINT uq_voto_unico UNIQUE (enquete_id, funcionario_id, opcao_id)
);

-- Índices para votos
CREATE INDEX idx_votos_enquete ON votos_enquete(enquete_id);
CREATE INDEX idx_votos_funcionario ON votos_enquete(funcionario_id);
CREATE INDEX idx_votos_opcao ON votos_enquete(opcao_id);

-- Tabela de reconhecimento entre colaboradores
CREATE TABLE reconhecimentos_colaborador (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    remetente_id UUID NOT NULL,
    destinatario_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL DEFAULT 'PARABENS',
    mensagem VARCHAR(1000) NOT NULL,
    motivo VARCHAR(500),
    data_reconhecimento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    publico BOOLEAN NOT NULL DEFAULT true,
    ativo BOOLEAN NOT NULL DEFAULT true,
    
    CONSTRAINT fk_reconhecimento_remetente FOREIGN KEY (remetente_id) REFERENCES funcionarios(id),
    CONSTRAINT fk_reconhecimento_destinatario FOREIGN KEY (destinatario_id) REFERENCES funcionarios(id),
    CONSTRAINT chk_reconhecimento_tipo_valido CHECK (tipo IN (
        'PARABENS', 'OBRIGADO', 'EXCELENTE_TRABALHO', 'COLABORACAO',
        'INOVACAO', 'LIDERANCA', 'MELHORIA_CONTINUA', 'ESPIRITO_EQUIPE'
    )),
    CONSTRAINT chk_reconhecimento_mensagem_nao_vazia CHECK (LENGTH(TRIM(mensagem)) > 0),
    CONSTRAINT chk_reconhecimento_diferentes_pessoas CHECK (remetente_id != destinatario_id)
);

-- Índices para reconhecimentos
CREATE INDEX idx_reconhecimentos_remetente ON reconhecimentos_colaborador(remetente_id);
CREATE INDEX idx_reconhecimentos_destinatario ON reconhecimentos_colaborador(destinatario_id);
CREATE INDEX idx_reconhecimentos_data ON reconhecimentos_colaborador(data_reconhecimento DESC);
CREATE INDEX idx_reconhecimentos_ativos ON reconhecimentos_colaborador(ativo) WHERE ativo = true;

-- Inserir dados iniciais de exemplo
INSERT INTO postagens_mural (autor_id, titulo, conteudo, tipo, visibilidade) 
SELECT id, 'Bem-vindos ao Mural Digital!', 
       'Este é o nosso novo espaço de comunicação interna. Aqui você pode compartilhar notícias, eventos, reconhecimentos e muito mais!',
       'ANUNCIO', 'PUBLICO'
FROM funcionarios 
WHERE cpf = '00000000000' OR email LIKE '%admin%' 
LIMIT 1;

INSERT INTO postagens_mural (autor_id, titulo, conteudo, tipo) 
SELECT id, 'Dica do dia: Organização', 
       'Que tal organizar sua mesa de trabalho? Um ambiente organizado aumenta a produtividade!',
       'DICA'
FROM funcionarios 
WHERE cpf = '00000000000' OR email LIKE '%admin%' 
LIMIT 1;

-- Comentários para documentação
COMMENT ON TABLE postagens_mural IS 'Tabela de postagens no mural digital da empresa';
COMMENT ON COLUMN postagens_mural.tipo IS 'Tipo da postagem conforme enum TipoPostagem';
COMMENT ON COLUMN postagens_mural.visibilidade IS 'Visibilidade da postagem conforme enum VisibilidadePostagem';

COMMENT ON TABLE comentarios_postagem IS 'Tabela de comentários nas postagens do mural';
COMMENT ON COLUMN comentarios_postagem.comentario_pai_id IS 'Para comentários em resposta a outros comentários';

COMMENT ON TABLE curtidas_postagem IS 'Tabela de reações (likes) nas postagens';
COMMENT ON COLUMN curtidas_postagem.tipo_reacao IS 'Tipo de reação conforme enum TipoReacao';

COMMENT ON TABLE enquetes_mural IS 'Tabela de enquetes e pesquisas de clima';
COMMENT ON COLUMN enquetes_mural.anonima IS 'Se os votos são anônimos';
COMMENT ON COLUMN enquetes_mural.permite_multipla_escolha IS 'Se permite selecionar múltiplas opções';

COMMENT ON TABLE reconhecimentos_colaborador IS 'Tabela de sistema de reconhecimento entre colegas';
COMMENT ON COLUMN reconhecimentos_colaborador.tipo IS 'Tipo de reconhecimento conforme enum TipoReconhecimento';

-- Criar view para dashboard de engajamento
CREATE OR REPLACE VIEW view_dashboard_engajamento AS
SELECT 
    'POSTAGENS' as tipo,
    COUNT(*) as quantidade,
    COUNT(DISTINCT autor_id) as usuarios_unicos
FROM postagens_mural WHERE ativa = true
UNION ALL
SELECT 
    'COMENTARIOS',
    COUNT(*),
    COUNT(DISTINCT autor_id)
FROM comentarios_postagem WHERE ativo = true
UNION ALL
SELECT 
    'CURTIDAS',
    COUNT(*),
    COUNT(DISTINCT funcionario_id)
FROM curtidas_postagem
UNION ALL
SELECT 
    'ENQUETES',
    COUNT(*),
    COUNT(DISTINCT autor_id)
FROM enquetes_mural WHERE ativa = true
UNION ALL
SELECT 
    'VOTOS',
    COUNT(*),
    COUNT(DISTINCT funcionario_id)
FROM votos_enquete
UNION ALL
SELECT 
    'RECONHECIMENTOS',
    COUNT(*),
    COUNT(DISTINCT remetente_id)
FROM reconhecimentos_colaborador WHERE ativo = true;

COMMENT ON VIEW view_dashboard_engajamento IS 'View consolidada para dashboard de métricas de engajamento';