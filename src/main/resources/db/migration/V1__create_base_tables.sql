-- Flyway Migration: Tabelas base do sistema

-- Tabela de departamentos
CREATE TABLE IF NOT EXISTS departamentos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    responsavel_id UUID,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de cargos
CREATE TABLE IF NOT EXISTS cargos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT,
    salario_base DECIMAL(10, 2),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de equipes
CREATE TABLE IF NOT EXISTS equipes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(100) NOT NULL,
    supervisor_id UUID,
    departamento_id UUID,
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Tabela de funcionários
CREATE TABLE IF NOT EXISTS funcionarios (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    telefone VARCHAR(20),
    data_nascimento DATE,
    data_admissao DATE NOT NULL,
    data_demissao DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    departamento_id UUID REFERENCES departamentos(id),
    cargo_id UUID REFERENCES cargos(id),
    equipe_id UUID REFERENCES equipes(id),
    salario_base DECIMAL(10, 2),
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Índices para performance
CREATE INDEX idx_funcionarios_cpf ON funcionarios(cpf);
CREATE INDEX idx_funcionarios_email ON funcionarios(email);
CREATE INDEX idx_funcionarios_status ON funcionarios(status);
CREATE INDEX idx_funcionarios_departamento ON funcionarios(departamento_id);
CREATE INDEX idx_funcionarios_cargo ON funcionarios(cargo_id);
CREATE INDEX idx_funcionarios_equipe ON funcionarios(equipe_id);
