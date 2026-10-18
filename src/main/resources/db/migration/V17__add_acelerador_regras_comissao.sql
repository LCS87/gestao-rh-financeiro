-- Fase 4: Regras de Comissões — aceleradores e campanhas
ALTER TABLE regras_comissao
    ADD COLUMN IF NOT EXISTS acelerador_percentual NUMERIC(5,2);

ALTER TABLE regras_comissao
    ADD COLUMN IF NOT EXISTS meta_minima_percentual NUMERIC(5,2);
