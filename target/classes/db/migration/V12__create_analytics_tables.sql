-- Flyway Migration V12: Tabelas para People Analytics e Dashboard

-- Tabela de indicadores calculados (cache para performance)
CREATE TABLE IF NOT EXISTS analytics_indicators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    indicador VARCHAR(50) NOT NULL, -- ex: 'rotatividade', 'absenteismo', 'custo_por_funcionario'
    valor NUMERIC(19, 4) NOT NULL,
    periodo VARCHAR(7) NOT NULL, -- YYYY-MM
    departamento_id UUID REFERENCES departamentos(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(indicador, periodo, departamento_id)
);

CREATE INDEX IF NOT EXISTS idx_analytics_indicador ON analytics_indicators(indicador);
CREATE INDEX IF NOT EXISTS idx_analytics_periodo ON analytics_indicators(periodo);

-- Tabela de métricas diárias
CREATE TABLE IF NOT EXISTS analytics_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    metrica VARCHAR(50) NOT NULL, -- ex: 'admissoes', 'demissoes', 'faltas'
    valor INTEGER NOT NULL DEFAULT 0,
    data DATE NOT NULL,
    departamento_id UUID REFERENCES departamentos(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_analytics_metrica ON analytics_metrics(metrica);
CREATE INDEX IF NOT EXISTS idx_analytics_data ON analytics_metrics(data);
