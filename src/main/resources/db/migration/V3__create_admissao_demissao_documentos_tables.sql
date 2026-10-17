-- Flyway Migration: Tabelas de Admissão, Demissão e Documentos

-- Tabela de admissões
CREATE TABLE IF NOT EXISTS admissoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL UNIQUE REFERENCES funcionarios(id),
    data_admissao DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    rg_entregue BOOLEAN DEFAULT FALSE,
    cpf_entregue BOOLEAN DEFAULT FALSE,
    ctps_entregue BOOLEAN DEFAULT FALSE,
    comprovante_residencia_entregue BOOLEAN DEFAULT FALSE,
    titulo_eleitor_entregue BOOLEAN DEFAULT FALSE,
    certificado_reservista BOOLEAN DEFAULT FALSE,
    exame_medico_realizado BOOLEAN DEFAULT FALSE,
    exame_medico_validade DATE,
    contrato_assinado BOOLEAN DEFAULT FALSE,
    data_assinatura_contrato DATE,
    observacoes VARCHAR(1000),
    responsavel_admissao_id UUID,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de demissões
CREATE TABLE IF NOT EXISTS demissoes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL UNIQUE REFERENCES funcionarios(id),
    data_demissao DATE NOT NULL,
    tipo_demissao VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'INICIADA',
    aviso_previo_trabalhado BOOLEAN DEFAULT FALSE,
    aviso_previo_indenizado BOOLEAN DEFAULT FALSE,
    exame_demissional_realizado BOOLEAN DEFAULT FALSE,
    data_exame_demissional DATE,
    motivo VARCHAR(1000),
    observacoes VARCHAR(2000),
    saldo_salario DECIMAL(10, 2),
    ferias_vencidas DECIMAL(10, 2),
    ferias_proporcionais DECIMAL(10, 2),
    decimo_terceiro DECIMAL(10, 2),
    decimo_terceiro_proporcional DECIMAL(10, 2),
    aviso_previo_indenizado_valor DECIMAL(10, 2),
    fgts DECIMAL(10, 2),
    multa_fgts DECIMAL(10, 2),
    valor_total_rescisao DECIMAL(10, 2),
    data_calculo DATE,
    responsavel_demissao_id UUID,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de documentos
CREATE TABLE IF NOT EXISTS documentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id),
    tipo_documento VARCHAR(30) NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    tamanho_arquivo BIGINT,
    tipo_mime VARCHAR(100),
    data_emissao DATE,
    data_validade DATE,
    data_upload DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'VALIDO',
    observacoes VARCHAR(1000),
    uploaded_by_id UUID,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Índices para performance
CREATE INDEX idx_admissoes_funcionario ON admissoes(funcionario_id);
CREATE INDEX idx_admissoes_status ON admissoes(status);

CREATE INDEX idx_demissoes_funcionario ON demissoes(funcionario_id);
CREATE INDEX idx_demissoes_status ON demissoes(status);
CREATE INDEX idx_demissoes_tipo ON demissoes(tipo_demissao);

CREATE INDEX idx_documentos_funcionario ON documentos(funcionario_id);
CREATE INDEX idx_documentos_tipo ON documentos(tipo_documento);
CREATE INDEX idx_documentos_status ON documentos(status);
CREATE INDEX idx_documentos_validade ON documentos(data_validade);
