-- Flyway Migration: Escalas, Ponto, Férias e Afastamentos (Fase 2)

-- Tabela de escalas de trabalho
CREATE TABLE IF NOT EXISTS escalas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(30) NOT NULL,
    descricao TEXT,
    horas_semanais DOUBLE PRECISION NOT NULL DEFAULT 44,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de turnos (vinculados à escala)
CREATE TABLE IF NOT EXISTS turnos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    escala_id UUID NOT NULL REFERENCES escalas(id) ON DELETE CASCADE,
    dia_semana INTEGER NOT NULL CHECK (dia_semana BETWEEN 1 AND 7),
    hora_entrada TIME NOT NULL,
    hora_saida TIME NOT NULL,
    intervalo_inicio TIME,
    intervalo_fim TIME,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE(escala_id, dia_semana)
);

-- Adiciona escala_id em funcionários
ALTER TABLE funcionarios ADD COLUMN IF NOT EXISTS escala_id UUID REFERENCES escalas(id);
CREATE INDEX IF NOT EXISTS idx_funcionarios_escala ON funcionarios(escala_id);

-- Tabela de registros de ponto
CREATE TABLE IF NOT EXISTS registros_ponto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    data DATE NOT NULL,
    hora_entrada TIME NOT NULL,
    hora_intervalo_inicio TIME,
    hora_intervalo_fim TIME,
    hora_saida TIME,
    observacoes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(funcionario_id, data)
);

CREATE INDEX IF NOT EXISTS idx_registros_ponto_funcionario ON registros_ponto(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_registros_ponto_data ON registros_ponto(data);

-- Tabela de férias
CREATE TABLE IF NOT EXISTS ferias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    dias INTEGER NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'INTEGRAL',
    status VARCHAR(20) NOT NULL DEFAULT 'AGENDADA',
    observacoes TEXT,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX IF NOT EXISTS idx_ferias_funcionario ON ferias(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_ferias_periodo ON ferias(data_inicio, data_fim);

-- Tabela de afastamentos
CREATE TABLE IF NOT EXISTS afastamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    motivo TEXT,
    observacoes TEXT,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX IF NOT EXISTS idx_afastamentos_funcionario ON afastamentos(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_afastamentos_periodo ON afastamentos(data_inicio, data_fim);
