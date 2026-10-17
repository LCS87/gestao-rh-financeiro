-- Flyway Migration: Parâmetros do Sistema e Logs de Auditoria

-- Tabela de Parâmetros
CREATE TABLE IF NOT EXISTS parametros_sistema (
    chave VARCHAR(100) PRIMARY KEY,
    valor TEXT NOT NULL,
    descricao TEXT,
    tipo VARCHAR(50) NOT NULL DEFAULT 'STRING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Auditoria
CREATE TABLE IF NOT EXISTS auditoria_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID,
    usuario_nome VARCHAR(255),
    acao VARCHAR(50) NOT NULL,
    tabela VARCHAR(100) NOT NULL,
    registro_id VARCHAR(255),
    detalhes TEXT,
    ip_address VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_auditoria_data ON auditoria_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria_logs(usuario_id);
