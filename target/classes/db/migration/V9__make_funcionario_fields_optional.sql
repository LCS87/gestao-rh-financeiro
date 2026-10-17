-- Flyway Migration V11: Tornar campos de Funcionario opcionais, exigindo apenas Nome, CPF e Data de Nascimento

-- Remover obrigatoriedade do Email
ALTER TABLE funcionarios ALTER COLUMN email DROP NOT NULL;

-- Remover obrigatoriedade da Data de Admissão
ALTER TABLE funcionarios ALTER COLUMN data_admissao DROP NOT NULL;

-- Adicionar obrigatoriedade da Data de Nascimento (caso haja registros com NULL, preenchemos retroativamente pra evitar crash)
UPDATE funcionarios SET data_nascimento = CURRENT_DATE WHERE data_nascimento IS NULL;
ALTER TABLE funcionarios ALTER COLUMN data_nascimento SET NOT NULL;
