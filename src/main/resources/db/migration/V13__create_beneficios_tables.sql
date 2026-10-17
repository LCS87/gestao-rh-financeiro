-- Migration V13: Criação das tabelas de Gestão de Benefícios
-- Data: 03/09/2026
-- Descrição: Tabelas para gerenciamento de benefícios e associação com funcionários

-- Tabela de benefícios disponíveis
CREATE TABLE beneficios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    tipo VARCHAR(50) NOT NULL,
    tipo_cobranca VARCHAR(50) NOT NULL,
    valor NUMERIC(15, 2),
    percentual_desconto NUMERIC(5, 2),
    ativo BOOLEAN NOT NULL DEFAULT true,
    obrigatorio BOOLEAN NOT NULL DEFAULT false,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT chk_beneficio_valor_percentual CHECK (
        (valor IS NOT NULL AND percentual_desconto IS NULL) OR
        (valor IS NULL AND percentual_desconto IS NOT NULL) OR
        (valor IS NULL AND percentual_desconto IS NULL)
    ),
    CONSTRAINT chk_beneficio_tipo_valido CHECK (tipo IN (
        'VALE_TRANSPORTE', 'VALE_REFEICAO', 'VALE_ALIMENTACAO',
        'PLANO_SAUDE', 'PLANO_ODONTOLOGICO', 'SEGURO_VIDA',
        'AUXILIO_CRECHE', 'AUXILIO_EDUCACAO', 'PARTICIPACAO_NOS_LUCROS',
        'PREVIDENCIA_PRIVADA', 'GYM_PASS', 'OUTROS'
    )),
    CONSTRAINT chk_beneficio_tipo_cobranca_valido CHECK (tipo_cobranca IN (
        'VALOR_FIXO', 'PERCENTUAL_SALARIO', 'VALOR_VARIAVEL',
        'EMPRESA_CUSTEIA_TOTAL', 'FUNCIONARIO_CUSTEIA_TOTAL', 'COMPARTILHADO'
    ))
);

-- Índices para melhor performance
CREATE INDEX idx_beneficios_nome ON beneficios(nome);
CREATE INDEX idx_beneficios_tipo ON beneficios(tipo);
CREATE INDEX idx_beneficios_ativo ON beneficios(ativo);

-- Tabela de associação entre funcionários e benefícios
CREATE TABLE funcionario_beneficios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL,
    beneficio_id UUID NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE,
    valor_funcionario NUMERIC(15, 2),
    valor_empresa NUMERIC(15, 2),
    ativo BOOLEAN NOT NULL DEFAULT true,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP,
    
    CONSTRAINT fk_funcionario_beneficio_funcionario 
        FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id),
    CONSTRAINT fk_funcionario_beneficio_beneficio 
        FOREIGN KEY (beneficio_id) REFERENCES beneficios(id),
    CONSTRAINT chk_funcionario_beneficio_datas CHECK (
        data_fim IS NULL OR data_inicio <= data_fim
    ),
    CONSTRAINT chk_funcionario_beneficio_valores CHECK (
        (valor_funcionario >= 0 AND valor_empresa >= 0)
    )
);

-- Índices para melhor performance
CREATE INDEX idx_funcionario_beneficios_funcionario ON funcionario_beneficios(funcionario_id);
CREATE INDEX idx_funcionario_beneficios_beneficio ON funcionario_beneficios(beneficio_id);
CREATE INDEX idx_funcionario_beneficios_ativo ON funcionario_beneficios(ativo);
CREATE INDEX idx_funcionario_beneficios_data_inicio ON funcionario_beneficios(data_inicio);
CREATE INDEX idx_funcionario_beneficios_data_fim ON funcionario_beneficios(data_fim);

-- Inserir benefícios padrão
INSERT INTO beneficios (nome, descricao, tipo, tipo_cobranca, valor, obrigatorio) VALUES
('Vale Transporte', 'Auxílio para transporte público ou combustível', 'VALE_TRANSPORTE', 'VALOR_FIXO', 250.00, true),
('Vale Refeição', 'Benefício para alimentação durante o expediente', 'VALE_REFEICAO', 'VALOR_FIXO', 600.00, true),
('Plano de Saúde', 'Cobertura médica básica para funcionário', 'PLANO_SAUDE', 'COMPARTILHADO', 350.00, true),
('Plano Odontológico', 'Cobertura odontológica básica', 'PLANO_ODONTOLOGICO', 'EMPRESA_CUSTEIA_TOTAL', 50.00, false),
('Seguro de Vida', 'Seguro de vida em grupo', 'SEGURO_VIDA', 'EMPRESA_CUSTEIA_TOTAL', 30.00, true),
('Auxílio Creche', 'Auxílio para filhos em idade pré-escolar', 'AUXILIO_CRECHE', 'VALOR_FIXO', 300.00, false),
('Gym Pass', 'Acesso a academias conveniadas', 'GYM_PASS', 'FUNCIONARIO_CUSTEIA_TOTAL', 80.00, false),
('Participação nos Lucros', 'PLR - Participação nos Lucros e Resultados', 'PARTICIPACAO_NOS_LUCROS', 'PERCENTUAL_SALARIO', NULL, false);

-- Comentários para documentação
COMMENT ON TABLE beneficios IS 'Tabela de cadastro de benefícios disponíveis na empresa';
COMMENT ON COLUMN beneficios.tipo IS 'Tipo do benefício conforme enum TipoBeneficio';
COMMENT ON COLUMN beneficios.tipo_cobranca IS 'Forma de cobrança conforme enum TipoCobrancaBeneficio';

COMMENT ON TABLE funcionario_beneficios IS 'Tabela de associação entre funcionários e benefícios ativos';
COMMENT ON COLUMN funcionario_beneficios.valor_funcionario IS 'Valor pago pelo funcionário (desconto na folha)';
COMMENT ON COLUMN funcionario_beneficios.valor_empresa IS 'Valor pago pela empresa (custo para a empresa)';