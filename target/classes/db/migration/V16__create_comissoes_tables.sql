-- Migration V16: Criar tabelas para comissões e metas
-- Data: 04/09/2026

-- Tabela de regras de comissão
CREATE TABLE regras_comissao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    tipo_comissao VARCHAR(50) NOT NULL,
    porcentagem DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    valor_fixo DECIMAL(15,2),
    valor_minimo DECIMAL(15,2),
    valor_maximo DECIMAL(15,2),
    aplicar_por_departamento BOOLEAN NOT NULL DEFAULT FALSE,
    departamento_id UUID,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_departamento_comissao FOREIGN KEY (departamento_id) 
        REFERENCES departamentos(id) ON DELETE SET NULL,
    CONSTRAINT chk_comissao_tipo CHECK (tipo_comissao IN ('PORCENTAGEM_VENDA', 'VALOR_FIXO', 'FAIXA_VENDA', 'BONUS_META'))
);

-- Tabela de metas
CREATE TABLE metas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL,
    tipo_meta VARCHAR(50) NOT NULL,
    valor_meta DECIMAL(15,2) NOT NULL,
    valor_alcancado DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    periodo_referencia VARCHAR(7) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVA',
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    data_conclusao DATE,
    
    CONSTRAINT fk_funcionario_meta FOREIGN KEY (funcionario_id) 
        REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_tipo_meta CHECK (tipo_meta IN ('MENSAL', 'SEMANAL', 'DIARIA', 'TRIMESTRAL')),
    CONSTRAINT unique_meta_funcionario_periodo UNIQUE (funcionario_id, tipo_meta, periodo_referencia)
);

-- Tabela de vendas com comissão
CREATE TABLE vendas_comissao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venda_id UUID NOT NULL,
    regra_comissao_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    valor_venda DECIMAL(15,2) NOT NULL,
    valor_comissao DECIMAL(15,2) NOT NULL,
    status_pagamento VARCHAR(50) NOT NULL DEFAULT 'PENDENTE',
    data_pagamento DATE,
    observacoes VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_venda FOREIGN KEY (venda_id) REFERENCES vendas(id) ON DELETE CASCADE,
    CONSTRAINT fk_regra_comissao FOREIGN KEY (regra_comissao_id) 
        REFERENCES regras_comissao(id) ON DELETE CASCADE,
    CONSTRAINT fk_venda_funcionario FOREIGN KEY (funcionario_id) 
        REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT chk_status_pagamento CHECK (status_pagamento IN ('PENDENTE', 'PAGO', 'CANCELADO'))
);

-- Tabela de comissões pagas (histórico)
CREATE TABLE comissoes_pagas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    venda_comissao_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    valor_comissao DECIMAL(15,2) NOT NULL,
    mes_referencia VARCHAR(7) NOT NULL,
    data_pagamento DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PAGO',
    observacoes VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_venda_comissao FOREIGN KEY (venda_comissao_id) 
        REFERENCES vendas_comissao(id) ON DELETE CASCADE,
    CONSTRAINT fk_comissao_funcionario FOREIGN KEY (funcionario_id) 
        REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT unique_comissao_venda UNIQUE (venda_comissao_id)
);

-- Tabela de metas individuais por funcionário
CREATE TABLE metas_funcionarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    meta_id UUID NOT NULL,
    funcionario_id UUID NOT NULL,
    valor_meta_individual DECIMAL(15,2) NOT NULL,
    valor_alcancado_individual DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    percentual_conclusao DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(50) NOT NULL DEFAULT 'ATIVA',
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP,
    data_conclusao DATE,
    
    CONSTRAINT fk_meta_funcionario FOREIGN KEY (meta_id) 
        REFERENCES metas(id) ON DELETE CASCADE,
    CONSTRAINT fk_funcionario_meta_individual FOREIGN KEY (funcionario_id) 
        REFERENCES funcionarios(id) ON DELETE CASCADE,
    CONSTRAINT unique_meta_funcionario UNIQUE (meta_id, funcionario_id)
);

-- Índices para performance
CREATE INDEX idx_regras_comissao_ativo ON regras_comissao(ativo);
CREATE INDEX idx_regras_comissao_departamento ON regras_comissao(departamento_id);
CREATE INDEX idx_metas_funcionario ON metas(funcionario_id, status, periodo_referencia);
CREATE INDEX idx_vendas_comissao_venda ON vendas_comissao(venda_id);
CREATE INDEX idx_vendas_comissao_funcionario ON vendas_comissao(funcionario_id, status_pagamento);
CREATE INDEX idx_comissoes_pagas_funcionario ON comissoes_pagas(funcionario_id, mes_referencia);
CREATE INDEX idx_comissoes_pagas_status ON comissoes_pagas(status);
CREATE INDEX idx_metas_funcionarios_meta ON metas_funcionarios(meta_id, status);

-- Comentários explicativos
COMMENT ON TABLE regras_comissao IS 'Tabela de regras de comissão configuráveis por departamento ou globais';
COMMENT ON TABLE metas IS 'Tabela de metas de vendas por funcionário';
COMMENT ON TABLE vendas_comissao IS 'Tabela de vinculação entre vendas e regras de comissão';
COMMENT ON TABLE comissoes_pagas IS 'Tabela de histórico de pagamentos de comissões';
COMMENT ON TABLE metas_funcionarios IS 'Tabela de metas individuais dentro de metas de equipe';
