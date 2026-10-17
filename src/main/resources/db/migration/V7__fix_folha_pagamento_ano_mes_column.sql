-- Flyway Migration: Corrige coluna ano_mes na tabela folhas_pagamento

-- Remove a coluna antiga e cria com tipo correto
DROP TABLE IF EXISTS folhas_pagamento CASCADE;

-- Recria a tabela com a estrutura correta
CREATE TABLE IF NOT EXISTS folhas_pagamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    funcionario_id UUID NOT NULL REFERENCES funcionarios(id) ON DELETE CASCADE,
    ano_mes VARCHAR(7) NOT NULL, -- Formato YYYY-MM para YearMonth
    salario_base NUMERIC(19,2) NOT NULL,
    
    -- Proventos
    horas_extras NUMERIC(19,2) DEFAULT 0,
    adicional_noturno NUMERIC(19,2) DEFAULT 0,
    comissoes NUMERIC(19,2) DEFAULT 0,
    bonus NUMERIC(19,2) DEFAULT 0,
    ferias NUMERIC(19,2) DEFAULT 0,
    decimo_terceiro NUMERIC(19,2) DEFAULT 0,
    outros_proventos NUMERIC(19,2) DEFAULT 0,
    
    -- Descontos
    inss NUMERIC(19,2) DEFAULT 0,
    irrf NUMERIC(19,2) DEFAULT 0,
    fgts NUMERIC(19,2) DEFAULT 0,
    vale_transporte NUMERIC(19,2) DEFAULT 0,
    vale_refeicao NUMERIC(19,2) DEFAULT 0,
    plano_saude NUMERIC(19,2) DEFAULT 0,
    outros_descontos NUMERIC(19,2) DEFAULT 0,
    
    -- Totais
    total_proventos NUMERIC(19,2) NOT NULL,
    total_descontos NUMERIC(19,2) NOT NULL,
    salario_liquido NUMERIC(19,2) NOT NULL,
    
    status VARCHAR(20) NOT NULL DEFAULT 'PROCESSANDO',
    data_processamento DATE,
    data_pagamento DATE,
    banco_pagamento VARCHAR(50),
    agencia_pagamento VARCHAR(20),
    conta_pagamento VARCHAR(20),
    observacoes TEXT,
    
    created_at DATE NOT NULL DEFAULT CURRENT_DATE,
    updated_at DATE NOT NULL DEFAULT CURRENT_DATE,
    
    UNIQUE(funcionario_id, ano_mes)
);

CREATE INDEX IF NOT EXISTS idx_folhas_pagamento_funcionario ON folhas_pagamento(funcionario_id);
CREATE INDEX IF NOT EXISTS idx_folhas_pagamento_ano_mes ON folhas_pagamento(ano_mes);
CREATE INDEX IF NOT EXISTS idx_folhas_pagamento_status ON folhas_pagamento(status);
