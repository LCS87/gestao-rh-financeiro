-- Migration V15: Criar tabelas para avaliação de desempenho
-- Data: 03/09/2026

-- Tabela de ciclos de avaliação
CREATE TABLE ciclos_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    tipo VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PLANEJADO',
    ano_referencia INTEGER NOT NULL,
    periodo_referencia INTEGER,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    data_inicio_autoavaliacao DATE,
    data_fim_autoavaliacao DATE,
    data_inicio_avaliacao_gestor DATE,
    data_fim_avaliacao_gestor DATE,
    data_inicio_feedback DATE,
    data_fim_feedback DATE,
    formulario_autoavaliacao_id UUID,
    formulario_avaliacao_gestor_id UUID,
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_formulario_autoavaliacao FOREIGN KEY (formulario_autoavaliacao_id) 
        REFERENCES formularios_avaliacao(id) ON DELETE SET NULL,
    CONSTRAINT fk_formulario_avaliacao_gestor FOREIGN KEY (formulario_avaliacao_gestor_id) 
        REFERENCES formularios_avaliacao(id) ON DELETE SET NULL
);

-- Tabela de formulários de avaliação
CREATE TABLE formularios_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    tipo VARCHAR(50) NOT NULL DEFAULT 'AUTOAVALIACAO',
    versao INTEGER NOT NULL DEFAULT 1,
    ativo BOOLEAN NOT NULL DEFAULT true,
    formulario_base_id UUID,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_formulario_base FOREIGN KEY (formulario_base_id) 
        REFERENCES formularios_avaliacao(id) ON DELETE SET NULL
);

-- Tabela de questões de avaliação
CREATE TABLE questoes_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    formulario_id UUID NOT NULL,
    enunciado VARCHAR(1000) NOT NULL,
    tipo VARCHAR(50) NOT NULL DEFAULT 'ESCALA_LIKERT',
    ordem INTEGER NOT NULL,
    opcoes VARCHAR(2000),
    peso INTEGER DEFAULT 1,
    obrigatoria BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_formulario FOREIGN KEY (formulario_id) 
        REFERENCES formularios_avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT unique_ordem_formulario UNIQUE (formulario_id, ordem)
);

-- Tabela de avaliações de desempenho
CREATE TABLE avaliacoes_desempenho (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ciclo_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    avaliador_id UUID,
    tipo VARCHAR(50) NOT NULL DEFAULT 'AUTOAVALIACAO',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    nota_final NUMERIC(5,2),
    percentual_conclusao NUMERIC(5,2) DEFAULT 0,
    data_inicio TIMESTAMP,
    data_conclusao TIMESTAMP,
    data_ultima_atualizacao TIMESTAMP,
    comentarios_avaliador VARCHAR(2000),
    comentarios_funcionario VARCHAR(2000),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_ciclo FOREIGN KEY (ciclo_id) 
        REFERENCES ciclos_avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT fk_funcionario FOREIGN KEY (funcionario_id) 
        REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_avaliador FOREIGN KEY (avaliador_id) 
        REFERENCES funcionarios(id) ON DELETE SET NULL,
    CONSTRAINT unique_avaliacao_ciclo_funcionario_tipo UNIQUE (ciclo_id, funcionario_id, tipo)
);

-- Tabela de respostas de avaliação
CREATE TABLE respostas_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliacao_id UUID NOT NULL,
    questao_id UUID NOT NULL,
    resposta VARCHAR(2000),
    nota NUMERIC(5,2),
    justificativa VARCHAR(1000),
    data_resposta TIMESTAMP NOT NULL DEFAULT NOW(),
    data_edicao TIMESTAMP,
    
    CONSTRAINT fk_avaliacao FOREIGN KEY (avaliacao_id) 
        REFERENCES avaliacoes_desempenho(id) ON DELETE CASCADE,
    CONSTRAINT fk_questao FOREIGN KEY (questao_id) 
        REFERENCES questoes_avaliacao(id) ON DELETE CASCADE,
    CONSTRAINT unique_resposta_questao UNIQUE (avaliacao_id, questao_id)
);

-- Tabela de planos de desenvolvimento individual
CREATE TABLE planos_desenvolvimento_individual (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliacao_id UUID NOT NULL UNIQUE,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(2000),
    status VARCHAR(50) NOT NULL DEFAULT 'RASCUNHO',
    data_inicio DATE NOT NULL,
    data_termino_previsto DATE NOT NULL,
    data_termino_real DATE,
    percentual_conclusao NUMERIC(5,2) DEFAULT 0,
    aprovador_id UUID,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    data_aprovacao TIMESTAMP,
    
    CONSTRAINT fk_avaliacao_pdi FOREIGN KEY (avaliacao_id) 
        REFERENCES avaliacoes_desempenho(id) ON DELETE CASCADE,
    CONSTRAINT fk_aprovador FOREIGN KEY (aprovador_id) 
        REFERENCES funcionarios(id) ON DELETE SET NULL
);

-- Tabela de metas do PDI
CREATE TABLE metas_pdi (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plano_desenvolvimento_id UUID NOT NULL,
    descricao VARCHAR(500) NOT NULL,
    acoes_especificas VARCHAR(2000),
    status VARCHAR(50) NOT NULL DEFAULT 'PLANEJADA',
    peso INTEGER NOT NULL DEFAULT 1,
    percentual_conclusao NUMERIC(5,2) DEFAULT 0,
    data_prevista_conclusao DATE NOT NULL,
    data_real_conclusao DATE,
    evidencias_conclusao VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_plano_desenvolvimento FOREIGN KEY (plano_desenvolvimento_id) 
        REFERENCES planos_desenvolvimento_individual(id) ON DELETE CASCADE
);

-- Tabela de feedbacks de avaliação
CREATE TABLE feedbacks_avaliacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    avaliacao_id UUID NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    comentarios_gestor VARCHAR(2000),
    comentarios_funcionario VARCHAR(2000),
    pontos_fortes VARCHAR(2000),
    pontos_melhoria VARCHAR(2000),
    metas_futuras VARCHAR(2000),
    data_feedback_gestor TIMESTAMP,
    data_feedback_funcionario TIMESTAMP,
    data_feedback_concluido TIMESTAMP,
    data_agendamento TIMESTAMP,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_avaliacao_feedback FOREIGN KEY (avaliacao_id) 
        REFERENCES avaliacoes_desempenho(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_ciclos_avaliacao_status ON ciclos_avaliacao(status, ativo);
CREATE INDEX idx_ciclos_avaliacao_periodo ON ciclos_avaliacao(ano_referencia, periodo_referencia);
CREATE INDEX idx_formularios_avaliacao_tipo ON formularios_avaliacao(tipo, ativo);
CREATE INDEX idx_questoes_avaliacao_formulario ON questoes_avaliacao(formulario_id, ordem);
CREATE INDEX idx_avaliacoes_desempenho_ciclo ON avaliacoes_desempenho(ciclo_id, status);
CREATE INDEX idx_avaliacoes_desempenho_funcionario ON avaliacoes_desempenho(funcionario_id, data_criacao);
CREATE INDEX idx_respostas_avaliacao_avaliacao ON respostas_avaliacao(avaliacao_id, questao_id);
CREATE INDEX idx_pdi_avaliacao ON planos_desenvolvimento_individual(avaliacao_id, status);
CREATE INDEX idx_metas_pdi_plano ON metas_pdi(plano_desenvolvimento_id, status);
CREATE INDEX idx_feedbacks_avaliacao_status ON feedbacks_avaliacao(status, data_agendamento);

-- Comentários explicativos
COMMENT ON TABLE ciclos_avaliacao IS 'Tabela de ciclos de avaliação de desempenho (trimestral, semestral, anual)';
COMMENT ON TABLE formularios_avaliacao IS 'Tabela de modelos de formulários de avaliação';
COMMENT ON TABLE questoes_avaliacao IS 'Tabela de questões que compõem os formulários de avaliação';
COMMENT ON TABLE avaliacoes_desempenho IS 'Tabela de avaliações de desempenho específicas de funcionários';
COMMENT ON TABLE respostas_avaliacao IS 'Tabela de respostas às avaliações de desempenho';
COMMENT ON TABLE planos_desenvolvimento_individual IS 'Tabela de planos de desenvolvimento individual (PDI)';
COMMENT ON TABLE metas_pdi IS 'Tabela de metas dentro dos planos de desenvolvimento individual';
COMMENT ON TABLE feedbacks_avaliacao IS 'Tabela de feedbacks após avaliações de desempenho';